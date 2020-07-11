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
package org.yx.http;

import java.util.Objects;

import org.yx.common.Ordered;
import org.yx.exception.SumkException;
import org.yx.http.handler.WebContext;

public abstract class WebFilter implements Ordered {

	private WebFilter next;

	public final void setNext(WebFilter next) {
		if (this.next != null) {
			SumkException.throwException(2343, "next已经赋值了，它是" + this.next);
		}
		this.next = Objects.requireNonNull(next);
	}

	protected final Object callNextFilter(WebContext ctx) throws Throwable {
		return this.next.doFilter(ctx);
	}

	public abstract Object doFilter(WebContext ctx) throws Throwable;

}
