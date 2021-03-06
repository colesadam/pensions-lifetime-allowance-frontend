/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package auth

import play.api.mvc.Results.Redirect
import play.api.mvc._
import config.FrontendAppConfig
import uk.gov.hmrc.play.frontend.auth.Verify
import uk.gov.hmrc.play.http.SessionKeys

import scala.concurrent.Future

class VerifyProvider(postSignInRedirectUrl: String, verifySignInUri: String) extends Verify {
  override def redirectToLogin(implicit request: Request[_]): Future[FailureResult] = {
    Future.successful(Redirect(login).withSession(
      SessionKeys.redirect -> postSignInRedirectUrl,
      SessionKeys.loginOrigin -> "PLA"
    ))
  }

  override def login: String = verifySignInUri
}
