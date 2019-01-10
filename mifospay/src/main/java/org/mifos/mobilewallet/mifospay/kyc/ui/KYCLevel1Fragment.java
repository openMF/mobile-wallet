package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel1Presenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCLevel1Fragment extends BaseFragment implements KYCContract.KYCLevel1View {

    @Inject
    KYCLevel1Presenter mPresenter;

    KYCContract.KYCLevel1Presenter mKYCLevel1Presenter;

    /**
     * Binding the Edit text Views which provide the user information like  first name, last name,
     * address1, address2 , mobilenumber, Date of birth
     * and it binds the submit button and the DatepickerDialog Box
     */

    @BindView(R.id.et_fname)  //Binding the first name edit text view
    EditText etFname;

    @BindView(R.id.et_lname)   //Binding the last name edit text view
    EditText etLname;

    @BindView(R.id.et_address1)   //Binding the addresss 1 edit textview
    EditText etAddress1;

    @BindView(R.id.et_address2)   // Binding the address 2 edit textview
    EditText etAddress2;

    @BindView(R.id.ccp_code)      //Binding the codepicker object
    CountryCodePicker ccpPhonecode;

    @BindView(R.id.et_mobile_number) //Binding the mobile number edit textview
    EditText etMobileNumber;

    @BindView(R.id.et_dob)     //Binding the date of birth edit textview
    EditText etDOB;

    @BindView(R.id.btn_submit)    //Binding the submit button
    Button btnSubmit;

    DatePickerDialog.OnDateSetListener date;    //Instantiating the DatePickerDialog
    private Calendar myCalendar;

    //Function for making the KYCLevel1 Fragment instance

    public static KYCLevel1Fragment newInstance() {

        Bundle args = new Bundle();

        KYCLevel1Fragment fragment = new KYCLevel1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        //Inflating the kyc level 1 layout
        View rootView = inflater.inflate(R.layout.fragment_kyc_lvl1, container, false);
        //Using the butterknife library to bind the views
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        //Setting the toolbar title to KYC REGISTERATION LEVEL 1 on opening KYC Level 1
        setToolbarTitle(Constants.KYC_REGISTRATION_LEVEL_1);

        //Creating a instance of the calender
        myCalendar = Calendar.getInstance();

        //Implementing DatePickerDialog listener
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = Constants.DD_MM_YY;
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                etDOB.setText(sdf.format(myCalendar.getTime()));
            }
        };

        ccpPhonecode.registerCarrierNumberEditText(etMobileNumber);
        ccpPhonecode.setCustomMasterCountries("IN,US");

        return rootView;
    }

    @OnClick(R.id.et_dob)
    public void onClickDOB() {
        new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }


    @OnClick(R.id.btn_submit)
    public void onClickSubmit() {
        showProgressDialog(Constants.PLEASE_WAIT);

        //Collecting the user inputs in KYC Level 1

        String fname = etFname.getText().toString();
        String lname = etLname.getText().toString();
        String address1 = etAddress1.getText().toString();
        String address2 = etAddress2.getText().toString();
        String phoneno = ccpPhonecode.getFullNumber();
        String dob = etDOB.getText().toString();

        //Calling the submit data function in KYCLevel1Presenter

        mKYCLevel1Presenter.submitData(fname, lname, address1, address2, phoneno, dob);
        Utils.hideSoftKeyboard(getActivity());
    }

    //setter function for setting the KYCLevel1 Presenter

    @Override
    public void setPresenter(KYCContract.KYCLevel1Presenter presenter) {
        mKYCLevel1Presenter = presenter;
    }

    //Function for showing the toast message

    @Override
    public void showToast(String s) {
        Toaster.show(getView(), s);
    }

    // Function for showing thw the progress dialog

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(message);
    }

    //fFunction for hiding the progress dialog

    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }


    @Override
    public void goBack() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }
}
