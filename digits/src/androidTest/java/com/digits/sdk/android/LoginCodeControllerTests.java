/*
 * Copyright (C) 2015 Twitter, Inc.
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
 *
 */

package com.digits.sdk.android;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginCodeControllerTests extends DigitsControllerTests<LoginCodeController> {
    @Captor
    ArgumentCaptor<Callback<VerifyAccountResponse>> callbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        controller = new DummyLoginCodeController(resultReceiver, sendButton, phoneEditText,
                sessionManager, digitsClient, REQUEST_ID, USER_ID, PHONE_WITH_COUNTRY_CODE, errors,
                new ActivityClassManagerImp(), scribeService, false);
    }

    public void testExecuteRequest_success() throws Exception {
        final DigitsCallback<DigitsSessionResponse> callback = executeRequest();
        final DigitsSessionResponse response = TestConstants.DIGITS_USER;
        final Result<DigitsSessionResponse> result = new Result(response, null);

        callback.success(result);
        verify(scribeService).success();
        verify(sessionManager).setActiveSession(DigitsSession.create(response,
                PHONE_WITH_COUNTRY_CODE));
        verify(sendButton).showFinish();
        final ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass
                (Runnable.class);
        verify(phoneEditText).postDelayed(runnableArgumentCaptor.capture(),
                eq(DigitsControllerImpl.POST_DELAY_MS));
        final Runnable runnable = runnableArgumentCaptor.getValue();
        runnable.run();

        final ArgumentCaptor<Bundle> bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(resultReceiver).send(eq(LoginResultReceiver.RESULT_OK),
                bundleArgumentCaptor.capture());
        assertEquals(PHONE_WITH_COUNTRY_CODE, bundleArgumentCaptor.getValue().getString
                (DigitsClient.EXTRA_PHONE));
    }

    public void testExecuteRequest_successWithEmailRequestSessionHasEmail() throws Exception {
        final DigitsSessionResponse response = TestConstants.DIGITS_USER;
        final Result<DigitsSessionResponse> result = new Result(response, null);
        final Result<VerifyAccountResponse> resultEmailRequest = new Result(
                TestConstants.getVerifyAccountResponse(), null);
        controller = new DummyLoginCodeController(resultReceiver, sendButton, phoneEditText,
                sessionManager, digitsClient, REQUEST_ID, USER_ID, PHONE_WITH_COUNTRY_CODE, errors,
                new ActivityClassManagerImp(), scribeService, true);

        final DigitsCallback<DigitsSessionResponse> callback = executeRequest();
        callback.success(result);
        verify(controller.getAccountService(null)).verifyAccount(callbackArgumentCaptor.capture());
        final Callback<VerifyAccountResponse> emailRequestCallback = callbackArgumentCaptor
                .getValue();
        emailRequestCallback.success(resultEmailRequest);
        verify(scribeService).success();
        final DigitsSession session = DigitsSession.create(
                TestConstants.getVerifyAccountResponse());
        verifyEmailRequest(session);
    }


    public void testExecuteRequest_successWithEmailRequestFailure() throws Exception {
        final DigitsSessionResponse response = TestConstants.DIGITS_USER;
        final Result<DigitsSessionResponse> result = new Result(response, null);
        controller = new DummyLoginCodeController(resultReceiver, sendButton, phoneEditText,
                sessionManager, digitsClient, REQUEST_ID, USER_ID, PHONE_WITH_COUNTRY_CODE, errors,
                new ActivityClassManagerImp(), scribeService, true);

        final DigitsCallback<DigitsSessionResponse> callback = executeRequest();
        callback.success(result);

        verify(controller.getAccountService(null)).verifyAccount(callbackArgumentCaptor.capture());
        final Callback<VerifyAccountResponse> emailRequestCallback = callbackArgumentCaptor
                .getValue();
        emailRequestCallback.failure(TestConstants.ANY_EXCEPTION);
        verify(scribeService).error(any(DigitsException.class));
        verify(phoneEditText).setError(null);
        verify(sendButton).showError();
    }

    public void testExecuteRequest_successWithEmailRequestSessionNoEmail() throws Exception {
        final DigitsSessionResponse response = TestConstants.DIGITS_USER;
        final Result<DigitsSessionResponse> result = new Result(response, null);
        final Result<VerifyAccountResponse> resultEmailRequest = new Result(
                TestConstants.getVerifyAccountResponseNoEmail(), null);
        final ComponentName emailRequestComponent = new ComponentName(context,
                controller.activityClassManager.getEmailRequestActivity());
        controller = new DummyLoginCodeController(resultReceiver, sendButton, phoneEditText,
                sessionManager, digitsClient, REQUEST_ID, USER_ID, PHONE_WITH_COUNTRY_CODE, errors,
                new ActivityClassManagerImp(), scribeService, true);

        final DigitsCallback<DigitsSessionResponse> callback = executeRequest();
        callback.success(result);
        verify(controller.getAccountService(null)).verifyAccount(callbackArgumentCaptor.capture());
        final Callback<VerifyAccountResponse> emailRequestCallback = callbackArgumentCaptor
                .getValue();
        emailRequestCallback.success(resultEmailRequest);
        final DigitsSession session = DigitsSession.create(
                TestConstants.getVerifyAccountResponseNoEmail());
        verify(sessionManager).setActiveSession(session);
        verify(context).startActivityForResult(intentArgumentCaptor.capture(), eq(DigitsActivity
                .REQUEST_CODE));
        final Intent intent = intentArgumentCaptor.getValue();
        assertEquals(emailRequestComponent, intent.getComponent());
        final Bundle bundle = intent.getExtras();
        assertTrue(BundleManager.assertContains(bundle, DigitsClient.EXTRA_PHONE,
                DigitsClient.EXTRA_RESULT_RECEIVER));
    }

    private void verifyEmailRequest(DigitsSession session) {
        verify(sessionManager).setActiveSession(session);
        verify(sendButton).showFinish();
        final ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass
                (Runnable.class);
        verify(phoneEditText).postDelayed(runnableArgumentCaptor.capture(),
                eq(DigitsControllerImpl.POST_DELAY_MS));
        final Runnable runnable = runnableArgumentCaptor.getValue();
        runnable.run();

        final ArgumentCaptor<Bundle> bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(resultReceiver).send(eq(LoginResultReceiver.RESULT_OK),
                bundleArgumentCaptor.capture());
        assertEquals(PHONE_WITH_COUNTRY_CODE, bundleArgumentCaptor.getValue().getString
                (DigitsClient.EXTRA_PHONE));
    }

    public void testExecuteRequest_requiresPinCode() throws Exception {
        final ComponentName pinCodeComponent = new ComponentName(context,
                controller.activityClassManager.getPinCodeActivity());
        final DigitsCallback<DigitsSessionResponse> callback = executeRequest();
        final Result<DigitsSessionResponse> response =
                new Result<>(new DigitsSessionResponse(), null);
        callback.success(response);
        verify(scribeService).success();
        verify(context).startActivityForResult(intentArgumentCaptor.capture(), eq(DigitsActivity
                .REQUEST_CODE));
        final Intent intent = intentArgumentCaptor.getValue();
        assertEquals(pinCodeComponent, intent.getComponent());
        final Bundle bundle = intent.getExtras();
        assertTrue(BundleManager.assertContains(bundle, DigitsClient.EXTRA_REQUEST_ID,
                DigitsClient.EXTRA_USER_ID, DigitsClient.EXTRA_PHONE,
                DigitsClient.EXTRA_RESULT_RECEIVER, DigitsClient.EXTRA_EMAIL));
    }

    DigitsCallback<DigitsSessionResponse> executeRequest() {
        when(phoneEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(CODE));
        final ArgumentCaptor<DigitsCallback> callbackArgumentCaptor = ArgumentCaptor.forClass
                (DigitsCallback.class);
        controller.executeRequest(context);
        verify(scribeService).click(DigitsScribeConstants.Element.SUBMIT);
        verify(sendButton).showProgress();
        verify(digitsClient).loginDevice(eq(REQUEST_ID), eq(USER_ID), eq(CODE),
                callbackArgumentCaptor.capture());
        return callbackArgumentCaptor.getValue();
    }

    public void testValidateInput_null() throws Exception {
        assertFalse(controller.validateInput(null));
    }

    public void testValidateInput_empty() throws Exception {
        assertFalse(controller.validateInput(EMPTY_CODE));
    }

}
