package com.shinhan.fcmexam;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by GYU on 2017-03-26.
 */

public class TabLayoutPager extends FragmentStatePagerAdapter {
    int _numOfTabs;

    public TabLayoutPager(FragmentManager fm, int numOfTabs) {
        super(fm);
        this._numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BankFragment bankFragment = new BankFragment(); // Fragment 는 알아서 만들자
                return bankFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return _numOfTabs;
    }
}