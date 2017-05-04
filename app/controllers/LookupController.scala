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

package controllers

import javax.inject.{Inject, Singleton}

import connectors.PLAConnector
import forms.PSALookupRequestForm.pSALookupRequestForm
import models.{PSALookupRequest, PSALookupResult}
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html._

import scala.concurrent.Future

@Singleton
class LookupController @Inject()(val messagesApi: MessagesApi,
                                 implicit val application: Application) extends FrontendController with play.api.i18n.I18nSupport {

  def displayLookupForm: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(pages.lookup.psa_lookup_form(pSALookupRequestForm)))
  }

  def submitLookupRequest: Action[AnyContent] = Action.async { implicit request =>
    pSALookupRequestForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(pages.lookup.psa_lookup_form(formWithErrors))),
      validFormData => {
        Future.successful(Redirect(routes.LookupController.displayLookupResults()))
      }
    )
  }

  def displayLookupResults: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(pages.lookup.psa_lookup_results(PSALookupResult("PSA12345678A", 7, 1, Some(BigDecimal.exact("1200.00"))))))
  }

}
