package org.mifos.mobilewallet.mifospay.kyc.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;

import javax.inject.Inject;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCLevel3Presenter implements KYCContract.KYCLevel3Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    private KYCContract.KYCLevel3View mKYCLevel1View;

    @Inject
    public KYCLevel3Presenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mKYCLevel1View = (KYCContract.KYCLevel3View) baseView;
        mKYCLevel1View.setPresenter(this);
    }

    /**
     * An override function from KYC Contract to check
     * if the given number is valid Aadhaar card number or not
     * by using Verhoeff Algorithm.
     */
    @Override
    public boolean validateAadhaarNumber(String num) {

        if (num.length() == 12) {

            // The multiplication table
            int[][] d = new int[][]
                    {
                            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
                            {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
                            {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
                            {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
                            {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
                            {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
                            {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
                            {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
                            {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
                    };

            // The permutation table
            int[][] p = new int[][]
                    {
                            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                            {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
                            {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
                            {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
                            {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
                            {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
                            {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
                            {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
                    };
            int c = 0;
            int[] myArray = stringToReversedIntArray(num);

            for (int i = 0; i < myArray.length; i++) {
                c = d[c][p[(i % 8)][myArray[i]]];
            }

            return (c == 0);
        } else {
            return false;
        }
    }

    /*
     * Converts a string to a reversed integer array.
     */
    private static int[] stringToReversedIntArray(String num) {

        int[] myArray = new int[num.length()];

        for (int i = 0; i < num.length(); i++) {
            myArray[i] = Integer.parseInt(num.substring(i, i + 1));
        }

        myArray = reverse(myArray);

        return myArray;

    }

    /*
     * Reverses an int array
     */
    private static int[] reverse(int[] myArray) {
        int[] reversed = new int[myArray.length];

        for (int i = 0; i < myArray.length; i++) {
            reversed[i] = myArray[myArray.length - (i + 1)];
        }

        return reversed;
    }
}
