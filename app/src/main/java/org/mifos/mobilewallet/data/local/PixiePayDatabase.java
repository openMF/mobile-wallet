package org.mifos.mobilewallet.data.local;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by naman on 11/7/17.
 */

@Database(name = PixiePayDatabase.NAME, version = PixiePayDatabase.VERSION)
public class PixiePayDatabase {

    public static final String NAME = "PixiePay";

    public static final int VERSION = 1;
}
