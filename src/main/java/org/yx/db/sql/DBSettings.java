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
package org.yx.db.sql;

import org.yx.conf.AppInfo;
import org.yx.log.Log;

public final class DBSettings {
	private static boolean FAIL_IF_PROPERTY_NOT_MAPPED;
	private static boolean FROM_CACHE;
	private static boolean TO_CACHE;

	private static int LIMIT_AS_NO_LIMIT;

	public static boolean failIfPropertyNotMapped() {
		return FAIL_IF_PROPERTY_NOT_MAPPED;
	}

	public static boolean fromCache() {
		return FROM_CACHE;
	}

	public static boolean toCache() {
		return TO_CACHE;
	}

	public static int asNoLimit() {
		return LIMIT_AS_NO_LIMIT;
	}

	public static synchronized void register() {
		if (LIMIT_AS_NO_LIMIT > 0) {
			return;
		}
		AppInfo.addObserver(info -> {
			try {
				FAIL_IF_PROPERTY_NOT_MAPPED = AppInfo.getBoolean("sumk.db.failIfPropertyNotMapped", true);
				FROM_CACHE = AppInfo.getBoolean("sumk.db.fromCache", true);
				TO_CACHE = AppInfo.getBoolean("sumk.db.toCache", true);
				LIMIT_AS_NO_LIMIT = AppInfo.getInt("sumk.db.asnolimit", 5000);
			} catch (Exception e) {
				Log.get("sumk.db").info(e.getMessage(), e);
			}
		});
	}
}