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
package org.yx.rpc.server.impl;

import org.yx.annotation.Bean;
import org.yx.common.CalleeNode;
import org.yx.rpc.RpcActionNode;
import org.yx.rpc.RpcActions;
import org.yx.rpc.RpcJson;
import org.yx.rpc.codec.ReqParamType;
import org.yx.rpc.codec.Request;
import org.yx.rpc.server.RequestHandler;
import org.yx.rpc.server.Response;
import org.yx.rpc.server.RpcVisitor;

@Bean
public class JsonedParamReqHandler implements RequestHandler {

	@Override
	public boolean handle(final Request req, Response resp) {
		if (!req.hasFeature(ReqParamType.REQ_PARAM_JSON)) {
			return false;
		}
		resp.sn(req.getSn());
		try {
			String api = req.getApi();
			RpcActionNode node = RpcActions.getActionNode(api);
			CalleeNode.checkNode(api, node);
			Object ret = RpcHandler.handle(node, new JsonedRpcVisitor(req));
			resp.json(RpcJson.operator().toJson(ret));
		} catch (Throwable e) {
			ServerExceptionHandler.handle(req, resp, e);
		}
		return true;
	}

	private static final class JsonedRpcVisitor extends RpcVisitor {

		public JsonedRpcVisitor(Request req) {
			super(req);
		}

		@Override
		public Object visit(RpcActionNode info) throws Throwable {
			return info.invokeByJsonArg(req.getJsonedParam());
		}

	};

}
