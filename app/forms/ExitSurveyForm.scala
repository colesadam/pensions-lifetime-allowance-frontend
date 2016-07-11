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

package forms

import play.api.i18n.Messages
import play.api.data._
import play.api.data.Forms._
import models._
import utils.Constants._
import common.Validation._

object ExitSurveyForm {
  val exitSurveyForm = Form(
    mapping(
      "phoneOrWrite" -> optional(text).verifying(Messages("pla.addedToPension.mandatoryErr"), {_.isDefined}),
      "phoneOrWriteNow" -> optional(text).verifying(Messages("pla.addedToPension.mandatoryErr"), {_.isDefined}),
      "anythingElse" -> optional(text).verifying(Messages("pla.addedToPension.mandatoryErr"), {_.isDefined}),
      "recommend" -> optional(text).verifying(Messages("pla.addedToPension.mandatoryErr"), {_.isDefined}),
      "satisfaction" -> optional(text).verifying(Messages("pla.addedToPension.mandatoryErr"), {_.isDefined}),
      "improvements" -> optional(text).verifying(Messages("pla.addedToPension.mandatoryErr"), {_.isDefined})
    )(ExitSurveyModel.apply)(ExitSurveyModel.unapply)
  )
}
