package com.paypal.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.test.selenium.SeleniumTestBase;
import com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.time.TimeUtils;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileReq;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileRequestType;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileResponseType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetBalanceReq;
import urn.ebay.api.PayPalAPI.GetBalanceRequestType;
import urn.ebay.api.PayPalAPI.GetBalanceResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.GetRecurringPaymentsProfileDetailsReq;
import urn.ebay.api.PayPalAPI.GetRecurringPaymentsProfileDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetRecurringPaymentsProfileDetailsResponseType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.api.PayPalAPI.TransactionSearchReq;
import urn.ebay.api.PayPalAPI.TransactionSearchRequestType;
import urn.ebay.api.PayPalAPI.TransactionSearchResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.AckCodeType;
import urn.ebay.apis.eBLBaseComponents.AddressType;
import urn.ebay.apis.eBLBaseComponents.AllowedPaymentMethodType;
import urn.ebay.apis.eBLBaseComponents.BillingAgreementDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingCodeType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodType;
import urn.ebay.apis.eBLBaseComponents.CountryCodeType;
import urn.ebay.apis.eBLBaseComponents.CreateRecurringPaymentsProfileRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.GetRecurringPaymentsProfileDetailsResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.PayerInfoType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionSearchResultType;
import urn.ebay.apis.eBLBaseComponents.RecurringPaymentsProfileDetailsType;
import urn.ebay.apis.eBLBaseComponents.RecurringPaymentsProfileStatusType;
import urn.ebay.apis.eBLBaseComponents.ScheduleDetailsType;
import urn.ebay.apis.eBLBaseComponents.SellerDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.SolutionTypeType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author jjYBdx4IL
 */
public class MerchantSDKTest extends SeleniumTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantSDKTest.class);
    public static final String PaymentActionCompleted = "PaymentActionCompleted";
    public static final String PaymentActionNotInitiated = "PaymentActionNotInitiated";
    private static Random RAND = new Random();

    private PayPalAPIInterfaceServiceService service = null;
    private final PayPalConfig config;
    private final PayPalTestAccountsConfig testAccountsConfig;

    public MerchantSDKTest() throws FileNotFoundException, IOException {
        config = new PayPalConfig();
        testAccountsConfig = new PayPalTestAccountsConfig();
    }

    @Before
    public void before() throws IOException {
        assumeTrue(Surefire.isSingleTestExecution()); // don't run tests with
                                                      // unstable external
                                                      // dependencies in CI

        config.read();
        testAccountsConfig.read();

        service = new PayPalAPIInterfaceServiceService(config.getSDKProps());
    }

    /**
     * The following test follows the example from <a href=
     * "https://developer.paypal.com/docs/classic/express-checkout/integration-guide/ECGettingStarted/">Getting
     * Started With Express Checkout</a>.
     * <p>
     * Worflow:
     * <ul>
     * <li>user clicks on "checkout with paypal" button
     * <li>Call SetExpressCheckout API
     * <li>redirect customer to paypal
     * <li>paypal login and data review by customer
     * <li>paypal redirects the customer to the merchant site
     * <li>Call GetExpressCheckoutDetails API
     * <li>customer confirms details/order
     * <li>Call DoExpressCheckoutPayment API
     * <li>show order confirmation
     * </ul>
     * <p>
     * The code has been taken from <a href=
     * "https://github.com/paypal/codesamples-java/blob/master/src/main/java/com/sample/merchant/">github</a>.
     */
    @Test
    public void testPayPalSetExpressCheckoutOrder() throws Exception {

        setTestName("PayPalExpressCheckoutOrder");

        // ## SetExpressCheckoutReq
        SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();

        // URL to which the buyer's browser is returned after choosing to pay
        // with PayPal. For digital goods, you must add JavaScript to this page
        // to close the in-context experience.
        // `Note:
        // PayPal recommends that the value be the final review page on which
        // the buyer confirms the order and payment or billing agreement.`
        setExpressCheckoutRequestDetails
            .setReturnURL("http://localhost/return");

        // URL to which the buyer is returned if the buyer does not approve the
        // use of PayPal to pay you. For digital goods, you must add JavaScript
        // to this page to close the in-context experience.
        // `Note:
        // PayPal recommends that the value be the original page on which the
        // buyer chose to pay with PayPal or establish a billing agreement.`
        setExpressCheckoutRequestDetails
            .setCancelURL("http://localhost/cancel");

        setExpressCheckoutRequestDetails.setSolutionType(SolutionTypeType.MARK);
        setExpressCheckoutRequestDetails.setAllowNote("0");

        // ### Payment Information
        // list of information about the payment
        List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();

        // information about the first payment
        PaymentDetailsType paymentDetails1 = new PaymentDetailsType();

        // Total cost of the transaction to the buyer. If shipping cost and tax
        // charges are known, include them in this value. If not, this value
        // should be the current sub-total of the order.
        //
        // If the transaction includes one or more one-time purchases, this
        // field must be equal to
        // the sum of the purchases. Set this field to 0 if the transaction does
        // not include a one-time purchase such as when you set up a billing
        // agreement for a recurring payment that is not immediately charged.
        // When the field is set to 0, purchase-specific fields are ignored.
        //
        // * `Currency Code` - You must set the currencyID attribute to one of
        // the
        // 3-character currency codes for any of the supported PayPal
        // currencies.
        // * `Amount`
        BasicAmountType orderTotal1 = new BasicAmountType(CurrencyCodeType.EUR,
            "2.00");
        paymentDetails1.setOrderTotal(orderTotal1);

        // How you want to obtain payment. When implementing parallel payments,
        // this field is required and must be set to `Order`. When implementing
        // digital goods, this field is required and must be set to `Sale`. If
        // the
        // transaction does not include a one-time purchase, this field is
        // ignored. It is one of the following values:
        //
        // * `Sale` - This is a final sale for which you are requesting payment
        // (default).
        // * `Authorization` - This payment is a basic authorization subject to
        // settlement with PayPal Authorization and Capture.
        // * `Order` - This payment is an order authorization subject to
        // settlement with PayPal Authorization and Capture.
        // `Note:
        // You cannot set this field to Sale in SetExpressCheckout request and
        // then change the value to Authorization or Order in the
        // DoExpressCheckoutPayment request. If you set the field to
        // Authorization or Order in SetExpressCheckout, you may set the field
        // to Sale.`
        paymentDetails1.setPaymentAction(PaymentActionCodeType.ORDER);

        // Unique identifier for the merchant. For parallel payments, this field
        // is required and must contain the Payer Id or the email address of the
        // merchant.
        SellerDetailsType sellerDetails1 = new SellerDetailsType();
        sellerDetails1.setPayPalAccountID(testAccountsConfig.seller1Email);
        paymentDetails1.setSellerDetails(sellerDetails1);

        // A unique identifier of the specific payment request, which is
        // required for parallel payments.
        paymentDetails1.setPaymentRequestID("PaymentRequest1");

        // `Address` to which the order is shipped, which takes mandatory
        // params:
        //
        // * `Street Name`
        // * `City`
        // * `State`
        // * `Country`
        // * `Postal Code`
        AddressType shipToAddress1 = new AddressType();
        shipToAddress1.setStreet1("Ape Way");
        shipToAddress1.setCityName("Austin");
        shipToAddress1.setStateOrProvince("TX");
        shipToAddress1.setCountry(CountryCodeType.US);
        shipToAddress1.setPostalCode("78750");

        // Your URL for receiving Instant Payment Notification (IPN) about this
        // transaction. If you do not specify this value in the request, the
        // notification URL from your Merchant Profile is used, if one exists.
        paymentDetails1.setNotifyURL("http://localhost/ipn");

        paymentDetails1.setShipToAddress(shipToAddress1);

        // information about the second payment
        PaymentDetailsType paymentDetails2 = new PaymentDetailsType();
        // Total cost of the transaction to the buyer. If shipping cost and tax
        // charges are known, include them in this value. If not, this value
        // should be the current sub-total of the order.
        //
        // If the transaction includes one or more one-time purchases, this
        // field must be equal to
        // the sum of the purchases. Set this field to 0 if the transaction does
        // not include a one-time purchase such as when you set up a billing
        // agreement for a recurring payment that is not immediately charged.
        // When the field is set to 0, purchase-specific fields are ignored.
        //
        // * `Currency Code` - You must set the currencyID attribute to one of
        // the
        // 3-character currency codes for any of the supported PayPal
        // currencies.
        // * `Amount`
        BasicAmountType orderTotal2 = new BasicAmountType(CurrencyCodeType.EUR,
            "4.00");
        paymentDetails2.setOrderTotal(orderTotal2);

        // How you want to obtain payment. When implementing parallel payments,
        // this field is required and must be set to `Order`. When implementing
        // digital goods, this field is required and must be set to `Sale`. If
        // the
        // transaction does not include a one-time purchase, this field is
        // ignored. It is one of the following values:
        //
        // * `Sale` - This is a final sale for which you are requesting payment
        // (default).
        // * `Authorization` - This payment is a basic authorization subject to
        // settlement with PayPal Authorization and Capture.
        // * `Order` - This payment is an order authorization subject to
        // settlement with PayPal Authorization and Capture.
        // `Note:
        // You cannot set this field to Sale in SetExpressCheckout request and
        // then change the value to Authorization or Order in the
        // DoExpressCheckoutPayment request. If you set the field to
        // Authorization or Order in SetExpressCheckout, you may set the field
        // to Sale.`
        paymentDetails2.setPaymentAction(PaymentActionCodeType.ORDER);

        // Unique identifier for the merchant. For parallel payments, this field
        // is required and must contain the Payer Id or the email address of the
        // merchant.
        SellerDetailsType sellerDetails2 = new SellerDetailsType();
        sellerDetails2.setPayPalAccountID(testAccountsConfig.seller1Email);
        paymentDetails2.setSellerDetails(sellerDetails2);

        // A unique identifier of the specific payment request, which is
        // required for parallel payments.
        paymentDetails2.setPaymentRequestID("PaymentRequest2");

        // `Address` to which the order is shipped, which takes mandatory
        // params:
        //
        // * `Street Name`
        // * `City`
        // * `State`
        // * `Country`
        // * `Postal Code`
        AddressType shipToAddress2 = new AddressType();
        shipToAddress2.setStreet1("Ape Way");
        shipToAddress2.setCityName("Austin");
        shipToAddress2.setStateOrProvince("TX");
        shipToAddress2.setCountry(CountryCodeType.US);
        shipToAddress2.setPostalCode("78750");

        // Your URL for receiving Instant Payment Notification (IPN) about this
        // transaction. If you do not specify this value in the request, the
        // notification URL from your Merchant Profile is used, if one exists.
        paymentDetails2.setNotifyURL("http://localhost/ipn");

        paymentDetails2.setShipToAddress(shipToAddress2);

        paymentDetailsList.add(paymentDetails1);
        paymentDetailsList.add(paymentDetails2);

        setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);

        SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
        SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(
            setExpressCheckoutRequestDetails);

        setExpressCheckoutReq
            .setSetExpressCheckoutRequest(setExpressCheckoutRequest);

        SetExpressCheckoutResponseType setExpressCheckoutResponse = service
            .setExpressCheckout(setExpressCheckoutReq);
        // ## Accessing response parameters
        // You can access the response parameters using getter methods in
        // response object as shown below
        // ### Success values
        if (setExpressCheckoutResponse.getAck().equals(AckCodeType.SUCCESS)) {

            // ### Redirecting to PayPal for authorization
            // Once you get the "Success" response, needs to authorise the
            // transaction by making buyer to login into PayPal. For that,
            // need to construct redirect url using EC token from response.
            // For example,
            // `redirectURL="https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+setExpressCheckoutResponse.getToken();`
            // Express Checkout Token
            LOG.info("EC Token:" + setExpressCheckoutResponse.getToken());
        } // ### Error Values
          // Access error values from error list using getter methods
        else {
            List<ErrorType> errorList = setExpressCheckoutResponse.getErrors();
            LOG.error("API Error Message : "
                + errorList.get(0).getLongMessage());
            fail();
        }
        // return setExpressCheckoutResponse;

        // ## GetExpressCheckoutDetailsReq
        GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();

        // A timestamped token, the value of which was returned by
        // `SetExpressCheckout` response.
        GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(
            setExpressCheckoutResponse.getToken());

        getExpressCheckoutDetailsReq
            .setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);

        GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = null;
        try {
            // ## Making API call
            // Invoke the appropriate method corresponding to API in service
            // wrapper object
            getExpressCheckoutDetailsResponse = service
                .getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
        } catch (SSLConfigurationException | InvalidCredentialException | IOException | HttpErrorException
            | InvalidResponseDataException | ClientActionRequiredException | MissingCredentialException
            | InterruptedException | OAuthException | ParserConfigurationException | SAXException e) {
            LOG.error("Error Message : " + e.getMessage());
            fail();
        }

        // check status before user accepts payment:
        assertEquals(AckCodeType.SUCCESS, getExpressCheckoutDetailsResponse.getAck());
        assertNull(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());
        assertEquals(PaymentActionNotInitiated, getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        LOG.info(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());

        visitPayPalWebsiteAndConfirmPayment(setExpressCheckoutResponse.getToken());

        getExpressCheckoutDetailsResponse = null;
        try {
            // ## Making API call
            // Invoke the appropriate method corresponding to API in service
            // wrapper object
            getExpressCheckoutDetailsResponse = service
                .getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
        } catch (SSLConfigurationException | InvalidCredentialException | IOException | HttpErrorException
            | InvalidResponseDataException | ClientActionRequiredException | MissingCredentialException
            | InterruptedException | OAuthException | ParserConfigurationException | SAXException e) {
            LOG.error("Error Message : " + e.getMessage());
            fail();
        }
        // ## Accessing response parameters
        // You can access the response parameters using getter methods in
        // response object as shown below
        // ### Success values
        if (getExpressCheckoutDetailsResponse.getAck().equals(AckCodeType.SUCCESS)) {

            // Unique PayPal Customer Account identification number. This
            // value will be null unless you authorize the payment by
            // redirecting to PayPal after `SetExpressCheckout` call.
            LOG.info("PayerID : "
                + getExpressCheckoutDetailsResponse
                    .getGetExpressCheckoutDetailsResponseDetails()
                    .getPayerInfo().getPayerID());

        } // ### Error Values
          // Access error values from error list using getter methods
        else {
            List<ErrorType> errorList = getExpressCheckoutDetailsResponse
                .getErrors();
            LOG.error("API Error Message : "
                + errorList.get(0).getLongMessage());
            fail();
        }
        // return getExpressCheckoutDetailsResponse;

        LOG.info(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertEquals(PaymentActionNotInitiated, getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertNotNull(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());

        // final step, nothing has been committed up to now!
        // ## DoExpressCheckoutPaymentReq
        DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();

        DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();

        // The timestamped token value that was returned in the
        // `SetExpressCheckout` response and passed in the
        // `GetExpressCheckoutDetails` request.
        doExpressCheckoutPaymentRequestDetails.setToken(setExpressCheckoutResponse.getToken());

        // Unique paypal buyer account identification number as returned in
        // `GetExpressCheckoutDetails` Response
        doExpressCheckoutPaymentRequestDetails.setPayerID(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());

        doExpressCheckoutPaymentRequestDetails
            .setPaymentDetails(paymentDetailsList);
        DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(
            doExpressCheckoutPaymentRequestDetails);
        doExpressCheckoutPaymentReq
            .setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

        DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = null;
        try {
            // ## Making API call
            // Invoke the appropriate method corresponding to API in service
            // wrapper object
            doExpressCheckoutPaymentResponse = service
                .doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
        } catch (ClientActionRequiredException | HttpErrorException | InvalidCredentialException
            | InvalidResponseDataException | MissingCredentialException | SSLConfigurationException | OAuthException
            | IOException | InterruptedException | ParserConfigurationException | SAXException e) {
            LOG.error("Error Message : " + e.getMessage());
            fail();
        }

        int nXactIds = 0;

        // ## Accessing response parameters
        // You can access the response parameters using getter methods in
        // response object as shown below
        // ### Success values
        if (doExpressCheckoutPaymentResponse.getAck().equals(AckCodeType.SUCCESS)) {

            // Transaction identification number of the transaction that was
            // created.
            // This field is only returned after a successful transaction
            // for DoExpressCheckout has occurred.
            if (doExpressCheckoutPaymentResponse
                .getDoExpressCheckoutPaymentResponseDetails()
                .getPaymentInfo() != null) {
                Iterator<PaymentInfoType> paymentInfoIterator = doExpressCheckoutPaymentResponse
                    .getDoExpressCheckoutPaymentResponseDetails()
                    .getPaymentInfo().iterator();
                while (paymentInfoIterator.hasNext()) {
                    PaymentInfoType paymentInfo = paymentInfoIterator
                        .next();
                    LOG.info("Transaction ID : "
                        + paymentInfo.getTransactionID());
                    assertNotNull(paymentInfo.getTransactionID());
                    nXactIds++;
                }
            }
        } // ### Error Values
          // Access error values from error list using getter methods
        else {
            List<ErrorType> errorList = doExpressCheckoutPaymentResponse
                .getErrors();
            LOG.error("API Error Message : "
                + errorList.get(0).getLongMessage());
            fail();
        }
        // return doExpressCheckoutPaymentResponse;

        assertEquals(2, nXactIds);

        // usually, the payment status is now "open" and we need to wait until
        // it is settled.
    }

    @Test
    public void testPayPalSetExpressCheckoutSale()
        throws WebElementNotFoundException, IOException, SSLConfigurationException, InvalidCredentialException,
        HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException,
        OAuthException, ParserConfigurationException, InterruptedException, SAXException {

        setTestName("PayPalExpressCheckoutSale");

        String invoiceIdPrefix = System.currentTimeMillis() + "A" + RAND.nextInt(Integer.MAX_VALUE) + "B";

        SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
        setExpressCheckoutRequestDetails
            .setReturnURL("http://localhost/return");
        setExpressCheckoutRequestDetails
            .setCancelURL("http://localhost/cancel");
        // require buyer to pay through a paypal account (-> payer_id):
        setExpressCheckoutRequestDetails.setSolutionType(SolutionTypeType.MARK);
        setExpressCheckoutRequestDetails.setAllowNote("0");
        setExpressCheckoutRequestDetails.setNoShipping("1");

        PaymentDetailsType paymentDetails1 = new PaymentDetailsType();
        BasicAmountType orderTotal1 = new BasicAmountType(CurrencyCodeType.EUR,
            "2.00");
        paymentDetails1.setOrderTotal(orderTotal1);
        paymentDetails1.setPaymentAction(PaymentActionCodeType.SALE);
        SellerDetailsType sellerDetails1 = new SellerDetailsType();
        sellerDetails1.setPayPalAccountID(testAccountsConfig.seller1Email);
        paymentDetails1.setSellerDetails(sellerDetails1);
        paymentDetails1.setPaymentRequestID("PaymentRequest1");
        paymentDetails1.setNotifyURL("http://localhost/ipn");
        paymentDetails1.setInvoiceID(invoiceIdPrefix + "1");
        paymentDetails1.setAllowedPaymentMethod(AllowedPaymentMethodType.INSTANTPAYMENTONLY);

        PaymentDetailsType paymentDetails2 = new PaymentDetailsType();
        BasicAmountType orderTotal2 = new BasicAmountType(CurrencyCodeType.EUR,
            "4.00");
        paymentDetails2.setOrderTotal(orderTotal2);
        paymentDetails2.setPaymentAction(PaymentActionCodeType.SALE);
        SellerDetailsType sellerDetails2 = new SellerDetailsType();
        sellerDetails2.setPayPalAccountID(testAccountsConfig.seller1Email);
        paymentDetails2.setSellerDetails(sellerDetails2);
        paymentDetails2.setPaymentRequestID("PaymentRequest2");
        paymentDetails2.setNotifyURL("http://localhost/ipn");
        paymentDetails2.setInvoiceID(invoiceIdPrefix + "2");
        paymentDetails2.setAllowedPaymentMethod(AllowedPaymentMethodType.INSTANTPAYMENTONLY);

        List<PaymentDetailsType> paymentDetailsList = new ArrayList<>();
        paymentDetailsList.add(paymentDetails1);
        paymentDetailsList.add(paymentDetails2);
        setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);

        SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
        SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(
            setExpressCheckoutRequestDetails);

        setExpressCheckoutReq
            .setSetExpressCheckoutRequest(setExpressCheckoutRequest);

        SetExpressCheckoutResponseType setExpressCheckoutResponse = service
            .setExpressCheckout(setExpressCheckoutReq);
        assertEquals(AckCodeType.SUCCESS, setExpressCheckoutResponse.getAck());
        LOG.info("EC Token:" + setExpressCheckoutResponse.getToken());

        GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();

        GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(
            setExpressCheckoutResponse.getToken());

        getExpressCheckoutDetailsReq
            .setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);

        GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = service
            .getExpressCheckoutDetails(getExpressCheckoutDetailsReq);

        // check status before user accepts payment:
        assertEquals(AckCodeType.SUCCESS, getExpressCheckoutDetailsResponse.getAck());
        assertNull(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());
        LOG.info(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertEquals(PaymentActionNotInitiated, getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());

        visitPayPalWebsiteAndConfirmPayment(setExpressCheckoutResponse.getToken());

        getExpressCheckoutDetailsResponse = service
            .getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
        assertEquals(AckCodeType.SUCCESS, getExpressCheckoutDetailsResponse.getAck());
        LOG.info("PayerID : "
            + getExpressCheckoutDetailsResponse
                .getGetExpressCheckoutDetailsResponseDetails()
                .getPayerInfo().getPayerID());

        LOG.info(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertEquals(PaymentActionNotInitiated, getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertNotNull(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());

        // final step, nothing has been committed up to now!
        LOG.info("doExpressCheckoutPayment");
        DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();

        DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();

        doExpressCheckoutPaymentRequestDetails.setToken(setExpressCheckoutResponse.getToken());

        doExpressCheckoutPaymentRequestDetails.setPayerID(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());

        doExpressCheckoutPaymentRequestDetails
            .setPaymentDetails(paymentDetailsList);
        DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(
            doExpressCheckoutPaymentRequestDetails);
        doExpressCheckoutPaymentReq
            .setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

        DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = service
            .doExpressCheckoutPayment(doExpressCheckoutPaymentReq);

        int nXactIds = 0;
        assertEquals(AckCodeType.SUCCESS, doExpressCheckoutPaymentResponse.getAck());

        assertNotNull(doExpressCheckoutPaymentResponse
            .getDoExpressCheckoutPaymentResponseDetails()
            .getPaymentInfo());
        Iterator<PaymentInfoType> paymentInfoIterator = doExpressCheckoutPaymentResponse
            .getDoExpressCheckoutPaymentResponseDetails()
            .getPaymentInfo().iterator();
        while (paymentInfoIterator.hasNext()) {
            PaymentInfoType paymentInfo = paymentInfoIterator
                .next();
            LOG.info("Transaction ID : "
                + paymentInfo.getTransactionID());
            assertNotNull(paymentInfo.getTransactionID());
            nXactIds++;
        }

        assertEquals(2, nXactIds);

        getExpressCheckoutDetailsResponse = service
            .getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
        assertEquals(AckCodeType.SUCCESS, getExpressCheckoutDetailsResponse.getAck());
        LOG.info("PayerID : "
            + getExpressCheckoutDetailsResponse
                .getGetExpressCheckoutDetailsResponseDetails()
                .getPayerInfo().getPayerID());

        LOG.info(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        // if we use the base currency of our merchant (seller) account and the
        // Sale type, the
        // payment is immediately settled:
        assertEquals(PaymentActionCompleted, getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertNotNull(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());

        // if we fail to process/store the doExpressCheckoutPayment response, we
        // need a fallback:
        TransactionSearchReq transactionSearchReq = new TransactionSearchReq();
        TransactionSearchRequestType transactionSearchRequest = new TransactionSearchRequestType();
        transactionSearchRequest.setStartDate("1980-01-01T00:00:00+0000");
        transactionSearchRequest.setInvoiceID(invoiceIdPrefix + "1");
        transactionSearchReq.setTransactionSearchRequest(transactionSearchRequest);
        TransactionSearchResponseType transactionSearchResponse = service
            .transactionSearch(transactionSearchReq);
        assertEquals(AckCodeType.SUCCESS, transactionSearchResponse.getAck());
        Iterator<PaymentTransactionSearchResultType> iterator = transactionSearchResponse
            .getPaymentTransactions().iterator();
        int nResults = 0;
        String transactionId = null;
        while (iterator.hasNext()) {
            PaymentTransactionSearchResultType searchResult = iterator
                .next();

            // Merchant's transaction ID.
            LOG.info("Transaction ID : " + searchResult.getTransactionID());
            LOG.info("Status         : " + searchResult.getStatus());
            LOG.info("Gross Amount   : " + searchResult.getGrossAmount().getValue());
            LOG.info("Fee Amount     : " + searchResult.getFeeAmount().getValue());
            LOG.info("Net Amount     : " + searchResult.getNetAmount().getValue());
            LOG.info("Payer          : " + searchResult.getPayer());

            transactionId = searchResult.getTransactionID();

            nResults++;
        }
        assertEquals(1, nResults);
        assertNotNull(transactionId);

        GetTransactionDetailsReq getTransactionDetailsReq = new GetTransactionDetailsReq();
        GetTransactionDetailsRequestType getTransactionDetailsRequest = new GetTransactionDetailsRequestType();

        getTransactionDetailsRequest.setTransactionID(transactionId);
        getTransactionDetailsReq
            .setGetTransactionDetailsRequest(getTransactionDetailsRequest);
        GetTransactionDetailsResponseType getTransactionDetailsResponse = service
            .getTransactionDetails(getTransactionDetailsReq);
        assertEquals(AckCodeType.SUCCESS, getTransactionDetailsResponse.getAck());

        PayerInfoType payer = getTransactionDetailsResponse
            .getPaymentTransactionDetails().getPayerInfo();
        LOG.info("Payer ID                  : " + payer.getPayerID());
        LOG.info("Payer Address Name        : " + payer.getAddress().getName());
        LOG.info("Payer Address Street 1    : " + payer.getAddress().getStreet1());
        LOG.info("Payer Address Street 2    : " + payer.getAddress().getStreet2());
        LOG.info("Payer Address Postal Code : " + payer.getAddress().getPostalCode());
        LOG.info("Payer Address Province    : " + payer.getAddress().getStateOrProvince());
        LOG.info("Payer Address City        : " + payer.getAddress().getCityName());
        LOG.info("Payer Address Country     : " + payer.getAddress().getCountryName());
    }

    @Test
    public void testPayPalRecurringPayments()
        throws WebElementNotFoundException, IOException, SSLConfigurationException, InvalidCredentialException,
        HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException,
        OAuthException, ParserConfigurationException, InterruptedException, SAXException {

        setTestName("PayPalRecurringPayments");

        String invoiceIdPrefix = System.currentTimeMillis() + "A" + RAND.nextInt(Integer.MAX_VALUE) + "B";
        String billingAgreementDescription = "FitnessMembership €9.99 per month";

        SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
        setExpressCheckoutRequestDetails
            .setReturnURL("http://localhost/return");
        setExpressCheckoutRequestDetails
            .setCancelURL("http://localhost/cancel");
        // require buyer to pay through a paypal account (-> payer_id):
        setExpressCheckoutRequestDetails.setSolutionType(SolutionTypeType.MARK);
        setExpressCheckoutRequestDetails.setAllowNote("0");
        setExpressCheckoutRequestDetails.setNoShipping("1");

        BillingAgreementDetailsType billingAgreement = new BillingAgreementDetailsType();
        billingAgreement.setBillingType(BillingCodeType.RECURRINGPAYMENTS);
        billingAgreement.setBillingAgreementDescription(billingAgreementDescription);

        List<BillingAgreementDetailsType> billingAgreementsList = new ArrayList<>();
        billingAgreementsList.add(billingAgreement);
        setExpressCheckoutRequestDetails.setBillingAgreementDetails(billingAgreementsList);

        SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
        SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(
            setExpressCheckoutRequestDetails);

        setExpressCheckoutReq
            .setSetExpressCheckoutRequest(setExpressCheckoutRequest);

        SetExpressCheckoutResponseType setExpressCheckoutResponse = service
            .setExpressCheckout(setExpressCheckoutReq);
        assertEquals(AckCodeType.SUCCESS, setExpressCheckoutResponse.getAck());
        LOG.info("EC Token:" + setExpressCheckoutResponse.getToken());

        visitPayPalWebsiteAndConfirmAgreement(setExpressCheckoutResponse.getToken(),
            testAccountsConfig.usBuyerEmail, testAccountsConfig.usBuyerPassword);

        GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
        GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(
            setExpressCheckoutResponse.getToken());
        getExpressCheckoutDetailsReq
            .setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);
        GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = service
            .getExpressCheckoutDetails(getExpressCheckoutDetailsReq);

        assertEquals(AckCodeType.SUCCESS, getExpressCheckoutDetailsResponse.getAck());
        LOG.info("PayerID : "
            + getExpressCheckoutDetailsResponse
                .getGetExpressCheckoutDetailsResponseDetails()
                .getPayerInfo().getPayerID());
        LOG.info(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertEquals(PaymentActionNotInitiated, getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus());
        assertNotNull(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getPayerInfo().getPayerID());
        assertTrue(getExpressCheckoutDetailsResponse
            .getGetExpressCheckoutDetailsResponseDetails()
            .getBillingAgreementAcceptedStatus());

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.HOUR_OF_DAY, 2); // avoid past dates (would generate an
                                          // api error)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        RecurringPaymentsProfileDetailsType recurringPaymentsProfileDetails = new RecurringPaymentsProfileDetailsType();
        recurringPaymentsProfileDetails.setBillingStartDate(TimeUtils.toISO8601WithSeconds(cal.getTime()));
        // sadly, we cannot define our own transaction id. only possibility up
        // to now is to use IPNs and
        // the following contained parameter to recover lost profile ids during
        // profile creation (api version 117):
        recurringPaymentsProfileDetails.setProfileReference(invoiceIdPrefix);

        ScheduleDetailsType scheduleDetails = new ScheduleDetailsType();
        scheduleDetails.setDescription(billingAgreementDescription);
        scheduleDetails.setMaxFailedPayments(1);
        BillingPeriodDetailsType billingPeriodDetails = new BillingPeriodDetailsType(BillingPeriodType.MONTH, 1,
            new BasicAmountType(CurrencyCodeType.EUR, "9.99"));
        scheduleDetails.setPaymentPeriod(billingPeriodDetails);

        CreateRecurringPaymentsProfileReq createRecurringPaymentsProfileReq = new CreateRecurringPaymentsProfileReq();
        CreateRecurringPaymentsProfileRequestType createRecurringPaymentsProfileRequest = new CreateRecurringPaymentsProfileRequestType();
        CreateRecurringPaymentsProfileRequestDetailsType createRecurringPaymentsProfileRequestDetails = new CreateRecurringPaymentsProfileRequestDetailsType();
        createRecurringPaymentsProfileRequest
            .setCreateRecurringPaymentsProfileRequestDetails(createRecurringPaymentsProfileRequestDetails);
        createRecurringPaymentsProfileReq
            .setCreateRecurringPaymentsProfileRequest(createRecurringPaymentsProfileRequest);
        createRecurringPaymentsProfileRequestDetails
            .setRecurringPaymentsProfileDetails(recurringPaymentsProfileDetails);
        createRecurringPaymentsProfileRequestDetails.setScheduleDetails(scheduleDetails);
        createRecurringPaymentsProfileRequestDetails.setToken(setExpressCheckoutResponse.getToken());

        // final step, nothing has been committed up to now!
        LOG.info("createRecurringPaymentsProfile");
        CreateRecurringPaymentsProfileResponseType createRecurringPaymentsProfileResponse = service
            .createRecurringPaymentsProfile(createRecurringPaymentsProfileReq);

        assertEquals(AckCodeType.SUCCESS, createRecurringPaymentsProfileResponse.getAck());
        assertEquals(RecurringPaymentsProfileStatusType.ACTIVEPROFILE, createRecurringPaymentsProfileResponse
            .getCreateRecurringPaymentsProfileResponseDetails().getProfileStatus());
        String profileId = createRecurringPaymentsProfileResponse
            .getCreateRecurringPaymentsProfileResponseDetails().getProfileID();
        assertNotNull(profileId);
        LOG.info("profile id = " + profileId);
        String xactionId = createRecurringPaymentsProfileResponse
            .getCreateRecurringPaymentsProfileResponseDetails().getTransactionID();
        LOG.info("transaction id = " + xactionId);

        GetRecurringPaymentsProfileDetailsReq getRecurringPaymentsProfileDetailsReq = new GetRecurringPaymentsProfileDetailsReq();
        GetRecurringPaymentsProfileDetailsRequestType getRecurringPaymentsProfileDetailsRequest = new GetRecurringPaymentsProfileDetailsRequestType();
        getRecurringPaymentsProfileDetailsRequest.setProfileID(profileId);
        getRecurringPaymentsProfileDetailsReq
            .setGetRecurringPaymentsProfileDetailsRequest(getRecurringPaymentsProfileDetailsRequest);
        GetRecurringPaymentsProfileDetailsResponseType getRecurringPaymentsProfileDetailsResponse = service
            .getRecurringPaymentsProfileDetails(getRecurringPaymentsProfileDetailsReq);
        assertEquals(AckCodeType.SUCCESS, getRecurringPaymentsProfileDetailsResponse.getAck());
        GetRecurringPaymentsProfileDetailsResponseDetailsType profileDetails = getRecurringPaymentsProfileDetailsResponse
            .getGetRecurringPaymentsProfileDetailsResponseDetails();
        LOG.info("profile description: " + profileDetails.getDescription());
        LOG.info("profile final payment due date: " + profileDetails.getFinalPaymentDueDate());
        LOG.info("profile aggregate amount: " + profileDetails.getAggregateAmount().getValue());
        LOG.info("profile autobill outstanding amount: " + profileDetails.getAutoBillOutstandingAmount());
    }

    @Test
    public void testGetBalance() throws Exception {

        GetBalanceReq getBalanceReq = new GetBalanceReq();
        GetBalanceRequestType getBalanceRequest = new GetBalanceRequestType();

        // Indicates whether to return all currencies. It is one of the
        // following values:
        //
        // * 0 – Return only the balance for the primary currency holding.
        // * 1 – Return the balance for each currency holding.
        getBalanceRequest.setReturnAllCurrencies("1");
        getBalanceReq.setGetBalanceRequest(getBalanceRequest);
        GetBalanceResponseType getBalanceResponse = service
            .getBalance(getBalanceReq);
        assertEquals(AckCodeType.SUCCESS, getBalanceResponse.getAck());
        int nResults = 0;
        Iterator<BasicAmountType> iterator2 = getBalanceResponse
            .getBalanceHoldings().iterator();
        while (iterator2.hasNext()) {
            BasicAmountType amount = iterator2.next();

            // Available balance and associated currency code for each currency
            // held, including the primary currency. The first currency is the
            // primary currency.
            LOG.info("Balance Holdings : " + amount.getValue() + " "
                + amount.getCurrencyID().getValue());
            nResults++;
        }
        assertEquals(1, nResults);
    }

    private void visitPayPalWebsiteAndConfirmPayment(String ecToken)
        throws WebElementNotFoundException, InterruptedException {
        visitPayPalWebsiteAndConfirmPayment(ecToken,
            testAccountsConfig.deBuyerEmail, testAccountsConfig.deBuyerPassword);
    }

    private void visitPayPalWebsiteAndConfirmPayment(String ecToken, String buyerEmail, String buyerPassword)
        throws WebElementNotFoundException, InterruptedException {
        LOG.info("visitPayPalWebsiteAndConfirmPayment()");

        String redirectUrl = config.ecRedirectUrl + ecToken;
        LOG.info("redirecting customer to: " + redirectUrl);
        LOG.info("using paypal customer test account " + buyerEmail + " - " + buyerPassword);
        getDriver().get(redirectUrl);

        WebElement loginButton = findElement("xpath://input[@id='submitLogin']");
        if (loginButton != null) {
            WebElement inputBuyerEmail = waitForElement("xpath://input[@id='login_email']");
            WebElement inputBuyerPassword = waitForElement("xpath://input[@id='login_password']");
            setInputFieldValue(inputBuyerEmail, buyerEmail);
            setInputFieldValue(inputBuyerPassword, buyerPassword);
            takeScreenshot();
            inputBuyerPassword.sendKeys(Keys.ENTER);
            WebElement continueButton = waitForElement("xpath://input[@id='continue']");
            takeScreenshot();
            continueButton.click();
        } else {
            WebElement iframe = waitForElement("xpath://iframe[@name='injectedUl']");
            getDriver().switchTo().frame(iframe);

            WebElement inputBuyerEmail = waitForElement("xpath://input[@id='email']", null, true);
            WebElement inputBuyerPassword = waitForElement("xpath://input[@id='password']", null, true);
            setInputFieldValue(inputBuyerEmail, buyerEmail);
            setInputFieldValue(inputBuyerPassword, buyerPassword);
            takeScreenshot();
            inputBuyerPassword.sendKeys(Keys.ENTER);
            
            getDriver().switchTo().parentFrame();
            WebElement continueButton = waitForElement("xpath://input[@type='submit']");
            takeScreenshot();
            continueButton.click();
        }

        // SEPA confirmation (not working, remove bank account from test buyer
        // as workaround)
        WebElement acceptButton = findElement("xpath://input[@id='accept.x']");
        if (acceptButton != null) {
            takeScreenshot();
            acceptButton.click();
        }

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return driver.getCurrentUrl().toLowerCase().contains("://localhost/");
            }
        });

        takeScreenshot();
        LOG.info("return url: " + getDriver().getCurrentUrl());
    }

    @SuppressWarnings("unused")
    private void visitPayPalWebsiteAndConfirmAgreement(String ecToken)
        throws WebElementNotFoundException, InterruptedException {
        visitPayPalWebsiteAndConfirmAgreement(ecToken,
            testAccountsConfig.deBuyerEmail, testAccountsConfig.deBuyerPassword);
    }

    private void visitPayPalWebsiteAndConfirmAgreement(String ecToken, String buyerEmail, String buyerPassword)
        throws WebElementNotFoundException, InterruptedException {
        LOG.info("visitPayPalWebsiteAndConfirmAgreement()");

        String redirectUrl = config.ecRedirectUrl + ecToken;
        LOG.info("redirecting customer to: " + redirectUrl);
        LOG.info("using paypal customer test account " + buyerEmail + " - " + buyerPassword);
        getDriver().get(redirectUrl);

        WebElement inputBuyerEmail = waitForElement("xpath://input[@id='login_email']");
        WebElement inputBuyerPassword = waitForElement("xpath://input[@id='login_password']");
        setInputFieldValue(inputBuyerEmail, buyerEmail);
        setInputFieldValue(inputBuyerPassword, buyerPassword);
        takeScreenshot();
        inputBuyerPassword.sendKeys(Keys.ENTER);

        // only once per buyer account:
        WebElement button = waitForElement("xpath://input[@id='agree']|//input[@id='continue']");
        if (button.getAttribute("id").equals("agree")) {
            WebElement esignOptCheckbox = waitForElement("xpath://input[@id='esignOpt']");
            esignOptCheckbox.click();
            takeScreenshot();
            button.click();
        }

        WebElement continueButton = waitForElement("xpath://input[@id='continue']");
        takeScreenshot();
        continueButton.click();

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return driver.getCurrentUrl().toLowerCase().contains("://localhost/");
            }
        });

        takeScreenshot();
        LOG.info("return url: " + getDriver().getCurrentUrl());
    }
}
