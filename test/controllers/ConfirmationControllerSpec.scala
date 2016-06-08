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


import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}
import org.scalatest.mock.MockitoSugar
import java.util.UUID
import play.api.i18n.Messages
import testHelpers._
import config.{FrontendAppConfig,FrontendAuthConnector}
import auth._

class ConfirmationControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

    object TestConfirmationController extends ConfirmationController {
        override lazy val applicationConfig = FrontendAppConfig
        override lazy val authConnector = MockAuthConnector
        override lazy val postSignInRedirectUrl = "http://localhost:9012/protect-your-lifetime-allowance/confirm-fp"
    }

    val sessionId = UUID.randomUUID.toString
    val fakeRequest = FakeRequest()

    val mockUsername = "mockuser"
    val mockUserId = "/auth/oid/" + mockUsername

    "ConfirmationController should be correctly initialised" in {
        ConfirmationController.authConnector shouldBe FrontendAuthConnector
    }

    "Calling the .confirmFP action" when {

        "navigated to " should {
            object DataItem extends AuthorisedFakeRequestTo(TestConfirmationController.confirmFP)
            "return a 200" in {
                status(DataItem.result) shouldBe 200
            }

            "take user to the Confirm FP page" in {
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.confirmFP16.pageHeading")
            }
        }
    }
}