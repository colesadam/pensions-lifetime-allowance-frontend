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

import java.util.UUID
import connectors.KeyStoreConnector
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}
import testHelpers._
import org.mockito.Matchers
import org.mockito.Mockito._
import scala.concurrent.Future
import config.{FrontendAppConfig,FrontendAuthConnector}
import models._
import auth._

class IP2016ControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

    val mockKeyStoreConnector = mock[KeyStoreConnector]

    object TestIP2016Controller extends IP2016Controller {
        override lazy val applicationConfig = FrontendAppConfig
        override lazy val authConnector = MockAuthConnector
        override lazy val postSignInRedirectUrl = "http://localhost:9012/protect-your-lifetime-allowance/apply-ip"
        override val keyStoreConnector: KeyStoreConnector = mockKeyStoreConnector
    }

    val sessionId = UUID.randomUUID.toString
    val fakeRequest = FakeRequest()

    val mockUsername = "mockuser"
    val mockUserId = "/auth/oid/" + mockUsername

    def keystoreFetchCondition[T](data: Option[T]): Unit = {
        when(mockKeyStoreConnector.fetchAndGetFormData[T](Matchers.anyString())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(data))
    }

    ///////////////////////////////////////////////
    // Initial Setup
    ///////////////////////////////////////////////
    "IP2016Controller should be correctly initialised" in {
        IP2016Controller.keyStoreConnector shouldBe KeyStoreConnector
        IP2016Controller.authConnector shouldBe FrontendAuthConnector
    }

    ///////////////////////////////////////////////
    // Pensions Taken
    ///////////////////////////////////////////////
    "In IP2016Controller calling the .pensionsTaken action" when {

        "not supplied with a stored model" should {

            object DataItem extends AuthorisedFakeRequestTo(TestIP2016Controller.pensionsTaken)
            "return 200" in {
                keystoreFetchCondition[PensionsTakenModel](None)
                status(DataItem.result) shouldBe 200
            }

            "take the user to the pensions taken page" in {
                keystoreFetchCondition[PensionsTakenModel](None)
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.pensionsTaken.pageHeading")
            }
        }

        "supplied with a stored test model" should {
            val testModel = new PensionsTakenModel(Some("yes"))
            object DataItem extends AuthorisedFakeRequestTo(TestIP2016Controller.pensionsTaken)

            "return 200" in {
                keystoreFetchCondition[PensionsTakenModel](Some(testModel))
                status(DataItem.result) shouldBe 200
            }

            "take the user to the pensions taken page" in {
                keystoreFetchCondition[PensionsTakenModel](None)
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.pensionsTaken.pageHeading")
            }

            "return some HTML that" should {

                "contain some text and use the character set utf-8" in {
                    keystoreFetchCondition[PensionsTakenModel](Some(testModel))
                    contentType(DataItem.result) shouldBe Some("text/html")
                    charset(DataItem.result) shouldBe Some("utf-8")
                }

                "have the radio option `yes` selected by default" in {
                    keystoreFetchCondition[PensionsTakenModel](Some(testModel))
                    DataItem.jsoupDoc.body.getElementById("pensionsTaken-yes").parent.classNames().contains("selected") shouldBe true
                }
            }
        }
    }

    "Submitting Pensions Taken data" when {

        "Submitting 'yes' in pensionsTakenForm" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTaken, ("pensionsTaken", "yes"))
            "return 303" in {status(DataItem.result) shouldBe 303}
            "redirect to pensions taken before" in { redirectLocation(DataItem.result) shouldBe Some(s"${routes.IP2016Controller.pensionsTakenBefore}") }
        }

        "Submitting 'no' in pensionsTakenForm" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTaken, ("pensionsTaken", "no"))
            "return 303" in { status(DataItem.result) shouldBe 303 }
            // "redirect to overseas pensions" in { redirectLocation(DataItem.result) shouldBe Some(s"${routes.IP2016Controller.overseasPensions}") }
        }

        "Submitting pensionsTakenForm with no data" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTaken, ("pensionsTaken", ""))
            "return 400" in { status(DataItem.result) shouldBe 400 }
            "fail with the correct error message" in {
                DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTaken.mandatoryErr"))
            }
        }
    }

    ///////////////////////////////////////////////
    // Pensions Taken Before
    ///////////////////////////////////////////////
    "In IP2016Controller calling the .pensionsTakenBefore action" when {

        "not supplied with a stored model" should {

            object DataItem extends AuthorisedFakeRequestTo(TestIP2016Controller.pensionsTakenBefore)
            "return 200" in {
                keystoreFetchCondition[PensionsTakenBeforeModel](None)
                status(DataItem.result) shouldBe 200
            }

            "take the user to the pensions taken before page" in {
                keystoreFetchCondition[PensionsTakenBeforeModel](None)
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.pensionsTakenBefore.pageHeading")
            }
        }

        "supplied with a stored test model" should {
            val testModel = new PensionsTakenBeforeModel("yes", Some(BigDecimal("1")))
            object DataItem extends AuthorisedFakeRequestTo(TestIP2016Controller.pensionsTakenBefore)

            "return 200" in {
                keystoreFetchCondition[PensionsTakenBeforeModel](Some(testModel))
                status(DataItem.result) shouldBe 200
            }

            "take the user to the pensions taken before page" in {
                keystoreFetchCondition[PensionsTakenBeforeModel](None)
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.pensionsTakenBefore.pageHeading")
            }
        }
    }

    "Submitting Pensions Taken Before data" when {

        "Submitting 'yes' in pensionsTakenBeforeForm" when {

            "amount is set as '1'" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBefore, ("pensionsTakenBefore", "Yes"), ("pensionsTakenBeforeAmt", "1"))
                "return 303" in {status(DataItem.result) shouldBe 303}
                "redirect to pensions taken between" in { redirectLocation(DataItem.result) shouldBe Some(s"${routes.IP2016Controller.pensionsTakenBetween}") }
            }

            "no amount is set" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBefore, ("pensionsTakenBefore", "Yes"), ("pensionsTakenBeforeAmt", ""))
                "return 400" in {status(DataItem.result) shouldBe 400}
                "fail with the correct error message" in {
                    DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBefore.errorQuestion"))
                }
            }

            "amount is set as '5.001'" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBefore, ("pensionsTakenBefore", "Yes"), ("pensionsTakenBeforeAmt", "5.001"))
                "return 400" in {status(DataItem.result) shouldBe 400}
                "fail with the correct error message" in {
                    DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBefore.errorDecimalPlaces"))
                }
            }

            "amount is set as '-25'" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBefore, ("pensionsTakenBefore", "Yes"), ("pensionsTakenBeforeAmt", "-25"))
                "return 400" in {status(DataItem.result) shouldBe 400}
                "fail with the correct error message" in {
                    DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBefore.errorNegative"))
                }
            }
        }

        "Submitting 'no' in pensionsTakenBeforeForm" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBefore, ("pensionsTakenBefore", "No"))
            "return 303" in { status(DataItem.result) shouldBe 303 }
            // TODO: redirect location not yet implemented in controller
            //"redirect to somewhere" in { redirectLocation(DataItem.result) shouldBe Some(s"${routes.IP2016Controller.someAction}") }
        }

        "Submitting pensionsTakenBeforeForm with no data" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBefore, ("pensionsTakenBefore", ""))
            "return 400" in { status(DataItem.result) shouldBe 400 }
            "fail with the correct error message" in {
                DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBefore.mandatoryErr"))
            }
        }
    }

    ///////////////////////////////////////////////
    // Pensions Taken Between
    ///////////////////////////////////////////////
    "In IP2016Controller calling the .pensionsTakenBetween action" when {

        "not supplied with a stored model" should {

            object DataItem extends AuthorisedFakeRequestTo(TestIP2016Controller.pensionsTakenBetween)
            "return 200" in {
                keystoreFetchCondition[PensionsTakenBetweenModel](None)
                status(DataItem.result) shouldBe 200
            }

            "take the user to the pensions taken between page" in {
                keystoreFetchCondition[PensionsTakenBetweenModel](None)
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.pensionsTakenBetween.pageHeading")
            }
        }

        "supplied with a stored test model" should {
            val testModel = new PensionsTakenBetweenModel("yes", Some(BigDecimal("1")))
            object DataItem extends AuthorisedFakeRequestTo(TestIP2016Controller.pensionsTakenBetween)

            "return 200" in {
                keystoreFetchCondition[PensionsTakenBetweenModel](Some(testModel))
                status(DataItem.result) shouldBe 200
            }

            "take the user to the pensions taken between page" in {
                keystoreFetchCondition[PensionsTakenBetweenModel](None)
                DataItem.jsoupDoc.body.getElementsByTag("h1").text shouldEqual Messages("pla.pensionsTakenBetween.pageHeading")
            }
        }
    }

    "Submitting Pensions Taken Between data" when {

        "Submitting 'yes' in pensionsTakenBetweenForm" when {

            // "amount is set as '1'" should {

            //     object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBetween, ("pensionsTakenBetween", "Yes"), ("pensionsTakenBetweenAmt", "1"))
            //     "return 303" in {status(DataItem.result) shouldBe 303}
            //     "redirect to pensions taken between" in { redirectLocation(DataItem.result) shouldBe Some(s"${routes.IP2016Controller.overseasPensions}") }
            // }

            "no amount is set" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBetween, ("pensionsTakenBetween", "Yes"), ("pensionsTakenBetweenAmt", ""))
                "return 400" in {status(DataItem.result) shouldBe 400}
                "fail with the correct error message" in {
                    DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBetween.errorQuestion"))
                }
            }

            "amount is set as '5.001'" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBetween, ("pensionsTakenBetween", "Yes"), ("pensionsTakenBetweenAmt", "5.001"))
                "return 400" in {status(DataItem.result) shouldBe 400}
                "fail with the correct error message" in {
                    DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBetween.errorDecimalPlaces"))
                }
            }

            "amount is set as '-25'" should {

                object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBetween, ("pensionsTakenBetween", "Yes"), ("pensionsTakenBetweenAmt", "-25"))
                "return 400" in {status(DataItem.result) shouldBe 400}
                "fail with the correct error message" in {
                    DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBetween.errorNegative"))
                }
            }
        }

        "Submitting 'no' in pensionsTakenBetweenForm" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBetween, ("pensionsTakenBetween", "No"))
            "return 303" in { status(DataItem.result) shouldBe 303 }
            // TODO: redirect location not yet implemented in controller
            //"redirect to somewhere" in { redirectLocation(DataItem.result) shouldBe Some(s"${routes.IP2016Controller.someAction}") }
        }

        "Submitting pensionsTakenBetweenForm with no data" should {

            object DataItem extends AuthorisedFakeRequestToPost(TestIP2016Controller.submitPensionsTakenBetween, ("pensionsTakenBetween", ""))
            "return 400" in { status(DataItem.result) shouldBe 400 }
            "fail with the correct error message" in {
                DataItem.jsoupDoc.getElementsByClass("error-notification").text should include (Messages("pla.pensionsTakenBetween.mandatoryErr"))
            }
        }
    }
}
