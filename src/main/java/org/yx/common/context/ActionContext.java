/**
 * Copyright (C) 2016 - 2030 youtongluan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yx.common.context;

import java.util.HashMap;
import java.util.Map;

import org.yx.conf.AppInfo;
import org.yx.exception.SumkException;

public final class ActionContext implements org.yx.common.Attachable, Cloneable {

	private static final String TEST = "sumk.test";

	private LogContext logContext;

	/**
	 * 用来做自增长的
	 */
	private int spanSeed;

	public boolean isTest() {
		return logContext.test;
	}

	private boolean parseTest(boolean isTest) {
		if (!isTest) {
			return false;
		}
		return AppInfo.getBoolean(TEST, false);
	}

	private ActionContext(String act, String traceId, String spanId, String userId, boolean isTest,
			Map<String, String> attachments) {
		this.logContext = LogContext.create(act, traceId, spanId, userId, this.parseTest(isTest), attachments);
	}

	private ActionContext(LogContext logContext) {
		this.logContext = logContext;
	}

	public String act() {
		return logContext.act;
	}

	public String traceId() {
		return logContext.traceId;
	}

	public String spanId() {
		return logContext.spanId;
	}

	private static final ThreadLocal<ActionContext> holder = new ThreadLocal<ActionContext>() {

		@Override
		protected ActionContext initialValue() {

			return new ActionContext(LogContext.EMPTY);
		}

	};

	public static ActionContext newContext(String act, String traceId, String thisIsTest) {
		boolean test = false;
		if (thisIsTest != null && thisIsTest.equals(AppInfo.get(TEST))) {
			test = true;
		}
		ActionContext c = new ActionContext(act, traceId, null, null, test, null);
		holder.set(c);
		return c;
	}

	public static ActionContext rpcContext(String act, String traceId, String spanId, String userId, boolean isTest,
			Map<String, String> attachments) {
		ActionContext c = new ActionContext(act, traceId, spanId, userId, isTest, attachments);
		holder.set(c);
		return c;
	}

	public static ActionContext get() {
		return holder.get();
	}

	public static void remove() {
		holder.remove();
	}

	public void setTraceIdIfAbsent(String traceId) {
		LogContext lc = this.logContext;
		if (lc.traceId != null) {
			return;
		}
		this.logContext = LogContext.create(lc.act, traceId, lc.spanId, lc.userId, lc.test, lc.attachments);
	}

	public String userId() {
		return logContext.userId;
	}

	public void userId(String userId) {
		LogContext lc = this.logContext;
		this.logContext = LogContext.create(lc.act, lc.traceId, lc.spanId, userId, lc.test, lc.attachments);
	}

	@Override
	public Map<String, String> attachmentView() {
		return logContext.attachments;
	}

	@Override
	public void setAttachments(Map<String, String> attachments) {
		this.logContext = LogContext.create(this.logContext, attachments);
	}

	@Override
	public void setAttachment(String key, String value) {
		Map<String, String> attachments = this.logContext.attachments;
		attachments = attachments == null ? new HashMap<>() : new HashMap<>(attachments);
		attachments.put(key, value);
		this.logContext = LogContext.create(this.logContext, attachments);
	}

	@Override
	public String getAttachment(String key) {
		Map<String, String> attachments = this.logContext.attachments;
		if (attachments == null) {
			return null;
		}
		return attachments.get(key);
	}

	public String nextSpanId() {
		LogContext lc = this.logContext;
		if (lc.traceId == null) {
			return "1";
		}
		int seed;
		synchronized (this) {
			seed = ++this.spanSeed;
		}
		String sp = lc.spanId;
		if (sp == null) {
			return String.valueOf(seed);
		}
		return new StringBuilder(lc.spanId).append('.').append(seed).toString();
	}

	public static void recover(ActionContext context) {
		holder.set(context);
	}

	public LogContext logContext() {
		return this.logContext;
	}

	public ActionContext clone() {
		try {
			return (ActionContext) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new SumkException(234235, "clone not supported");
		}
	}
}
