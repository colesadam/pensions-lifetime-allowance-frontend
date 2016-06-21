/*
 * Copyright 2016 HM Revenue & Customs
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

package controllers

import auth.AuthorisedForPLA
import config.{FrontendAppConfig,FrontendAuthConnector}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import connectors.KeyStoreConnector
import views.html._
import models._


object SummaryController extends SummaryController {
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val postSignInRedirectUrl = FrontendAppConfig.introductionUrl

  val keyStoreConnector = KeyStoreConnector
}

trait SummaryController extends FrontendController with AuthorisedForPLA {

  val keyStoreConnector : KeyStoreConnector

  val summaryIP16 = AuthorisedByAny.async { implicit user => implicit request =>
    keyStoreConnector.fetchAllUserData.map {
      case Some(data) => Ok(pages.ip2016.summary(1))
      case None => Ok(pages.introduction())
    }
  }

}
