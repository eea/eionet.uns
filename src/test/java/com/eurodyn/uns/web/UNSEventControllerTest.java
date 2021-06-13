package com.eurodyn.uns.web;

import com.eurodyn.uns.service.ServiceDispatcher;
import com.eurodyn.uns.service.UserBasicAuthenticationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UNSEventControllerTest {

    @Mock
    private UNSEventController unsEventController;

    @Mock
    UserBasicAuthenticationService userBasicAuthenticationService;

    @Mock
    ServiceDispatcher serviceDispatcher;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(userBasicAuthenticationService.checkUserAuthentication(Mockito.anyString())).thenReturn("testUser");
        when(serviceDispatcher.sendNotificationRDF(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        when(serviceDispatcher.sendNotification(Mockito.anyString(), Mockito.any())).thenReturn("");
        when(serviceDispatcher.canSubscribe(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(serviceDispatcher.createChannel(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        when(serviceDispatcher.makeSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn("");

        when(unsEventController.getUserBasicAuthenticationService()).thenReturn(userBasicAuthenticationService);
        when(unsEventController.getServiceDispatcher()).thenReturn(serviceDispatcher);
        doNothing().when(unsEventController).initServiceDispatcher(Mockito.anyString());
    }


    @Test
    public void testCreateChannel() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Basic token");
        mockRequest.addHeader("customHeader", "customValue");

        when(unsEventController.createChannel(mockRequest, "testChannel", "description")).thenCallRealMethod();

        String result = unsEventController.createChannel(mockRequest, "testChannel", "description");
        Assert.assertThat(result, is(""));

    }

    @Test
    public void testSendNotification() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Basic token");
        mockRequest.addHeader("customHeader", "customValue");

        mockRequest.setParameter("triples", "aced0005737200106a6176612e7574696c2e566563746f72d9977d5b803baf010300034900116361706163697479496e6372656d656e7449000c656c656d656e74436f756e745b000b656c656d656e74446174617400135b4c6a6176612f6c616e672f4f626a6563743b78700000000000000007757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c02000078700000000a7371007e000000000000000000037571007e00030000000a740046687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f6576656e74732f386234376535653030623531633763653730373436383134623066376362626174002f687474703a2f2f7777772e77332e6f72672f313939392f30322f32322d7264662d73796e7461782d6e732374797065740015446174612044696374696f6e617279206576656e7470707070707070787371007e000000000000000000037571007e00030000000a71007e0007740034687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e726466236576656e745f7479706574000f44617461736574206368616e67656470707070707070787371007e000000000000000000037571007e00030000000a71007e0007740038687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e72646623646566696e6974696f6e5f75726c740039687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f64617461736574732f6c61746573742f64656c6574655465737470707070707070787371007e000000000000000000037571007e00030000000a71007e0007740031687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e726466236461746173657474000a64656c6574655465737470707070707070787371007e000000000000000000037571007e00030000000a71007e0007740025687474703a2f2f7075726c2e6f72672f64632f656c656d656e74732f312e312f7469746c6574001d44442044617461736574206368616e6765642064656c6574655465737470707070707070787371007e000000000000000000037571007e00030000000a71007e000774002e687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e7264662375736572740008616e746861616e7470707070707070787371007e000000000000000000037571007e00030000000a71007e000774003b687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e72646623646566696e6974696f6e5f7374617475737400085265636f72646564707070707070707870707078");

        when(unsEventController.sendNotification(mockRequest, "testChannel")).thenCallRealMethod();

        String result = unsEventController.sendNotification(mockRequest, "testChannel");
        Assert.assertThat(result, is(""));
    }

    @Test
    public void testSendNotificationRDF() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Basic token");
        mockRequest.addHeader("customHeader", "customValue");

        when(unsEventController.sendNotificationRDF(mockRequest, "testChannel", "rdf")).thenCallRealMethod();

        String result = unsEventController.sendNotificationRDF(mockRequest, "testChannel", "rdf");
        Assert.assertThat(result, is(""));
    }

    @Test
    public void testCanSubscribe() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Basic token");
        mockRequest.addHeader("customHeader", "customValue");

        when(unsEventController.canSubscribe(mockRequest, "testChannel", "testUser")).thenCallRealMethod();

        Boolean result = unsEventController.canSubscribe(mockRequest, "testChannel", "testUser");
        Assert.assertThat(result, is(true));
    }

    @Test
    public void testMakeSubscription() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Basic token");
        mockRequest.addHeader("customHeader", "customValue");

        mockRequest.setParameter("filters", "aced0005737200106a6176612e7574696c2e566563746f72d9977d5b803baf010300034900116361706163697479496e6372656d656e7449000c656c656d656e74436f756e745b000b656c656d656e74446174617400135b4c6a6176612f6c616e672f4f626a6563743b78700000000000000001757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c02000078700000000a737200136a6176612e7574696c2e486173687461626c6513bb0f25214ae4b803000246000a6c6f6164466163746f724900097468726573686f6c6478703f4000000000000877080000000b00000002740034687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e726466236576656e745f7479706574000f44617461736574206368616e676564740031687474703a2f2f6c6f63616c686f73743a383038312f64617461646963742f736368656d612e726466236461746173657474000a64656c657465546573747870707070707070707078");

        when(unsEventController.makeSubscription(mockRequest, "testChannel", "testUser")).thenCallRealMethod();

        String result = unsEventController.makeSubscription(mockRequest, "testChannel", "testUser");
        Assert.assertThat(result, is(""));
    }
}
