/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package ai.api.twilio;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.twilio.sdk.verbs.Message;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

/**
 * This servlet receives callbacks from www.twilio.com with incoming SMS body,
 * sends this message to api.ai and generates instructions for www.twilio.com to
 * send reply SMS to sender with response generated by api.ai
 */
public class TwilioSmsServlet extends BaseTwilioServlet {
	private static final long serialVersionUID = -8510776154233631616L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TwilioSmsServlet.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String query = request.getParameter("Body");
		if (Strings.isNullOrEmpty(query)) {
			// Empty request
			return;
		}
		String openApiResponse = sendRequestToApiAi(query, request.getParameterMap());

		TwiMLResponse twiml = new TwiMLResponse();
		Message message = new Message(openApiResponse);
		try {
			twiml.append(message);
		} catch (TwiMLException e) {
			LOGGER.error(e.getMessage());
		}

		response.setContentType("application/xml");
		response.getWriter().print(twiml.toXML());
	}
}
