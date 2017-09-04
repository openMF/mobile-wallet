package org.mifos.mobilewallet.data.rbl.repository;

import android.util.Log;

import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.data.rbl.api.RblApiManager;
import org.mifos.mobilewallet.data.rbl.api.ResponseParser;

import java.io.IOException;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.functions.Func1;

/**
 * Created by naman on 22/6/17.
 */

@Singleton
public class RblRepository {

    private final RblApiManager rblApiManager;
    private final PreferencesHelper preferencesHelper;


    @Inject
    public RblRepository(RblApiManager rblApiManager,
                         PreferencesHelper preferencesHelper) {
        this.rblApiManager = rblApiManager;
        this.preferencesHelper = preferencesHelper;
    }

    public rx.Observable<Boolean> verifyPanNumber(String number) {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "{\"panInquiry\":" +
                        "{\"Header\":" +
                        "{\"TranID\":\"" + createTranId() + "\"," +
                        "\"Corp_ID\":\"HACKTEST\"," +
                        "\"Maker_ID\":\"8268117367062528\"," +
                        "\"Checker_ID\":\"6922292124712960\"," +
                        "\"Approver_ID\":\"989553554882560\"}," +
                        "\"Body\":{" +
                        "\"panNumbers\":[{\"pan1\":\"" + number + "\"}]}," +
                        "\"Signature\":{\"Signature\":\"leteswiw\"}}}");

        return rblApiManager.getPanApi().verifyPan(RblApiManager.CLIENT_ID,
                RblApiManager.CLIENT_SECRET, body)
                .map(new Func1<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean call(Response<ResponseBody> responseBodyResponse) {
                        try {
                            return ResponseParser
                                    .parsePanInquiryResponse(responseBodyResponse.body().string());
                        } catch (IOException e) {
                            Log.e("RblRespository", e.getMessage());
                            return false;
                        }
                    }
                });
    }

    //TODO Implement aadhar apis from rbl
    public rx.Observable<Boolean> verifyAadharNumber(String number) {
        return rx.Observable.just(true);
    }

    public rx.Observable<Boolean> generateAadharOtp() {
        return rx.Observable.just(true);
    }

    public rx.Observable<Boolean> verifyAadharOtp(String otp) {
        return rx.Observable.just(true);
    }

    private String createTranId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 15; i++ ) {
            sb.append((char) ('0' + random.nextInt(10)));
        }
        return sb.toString();
    }
}
