package net.numa08.dynamicfragmentstatepageradapter.support;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DynamicFragmentStatePagerAdapter extends PagerAdapter {


    private static final String TAG = "ifpa";
    private boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private Fragment mCurrentPrimaryItem = null;
    private Map<Fragment, Boolean> mFragmentIsDeleted = null;

    public DynamicFragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object
                + " v=" + ((Fragment) object).getView());
        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }
        mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (mFragments == null || mFragments.isEmpty()) {
            super.getItemPosition(object);
        }
        final Fragment fragment = (Fragment) object;
        final int position = mFragments.indexOf(fragment);
        if (mFragmentIsDeleted == null || mFragmentIsDeleted.isEmpty()) {
            return position;
        }
        final boolean isDeleted = mFragmentIsDeleted.get(fragment);
        if (!isDeleted) {
            return position;
        }
        mFragmentIsDeleted = null;
        return PagerAdapter.POSITION_NONE;
    }

    /***
     * 0 から始まる position の位置にある Fragment を削除します。データセットを更新する前にこのメソッドを呼び出してください。
     * notifyDataSetChanged は自動的に呼ばれます。
     * <code>
     * adapter.updateContents(/your contents); // Do not call notifyDataSetChanged
     * adapter.remove(position);
     * </code>
     *
     * @param position 0から始まるFragmentの位置。
     */
    public void removeItem(int position) {
        if (mFragments == null || mFragments.isEmpty()) {
            return;
        }
        if (position < 0 || position >= mFragments.size()) {
            throwIndexOutOfBoundsException(position);
        }
        mFragmentIsDeleted = new HashMap<>(mFragments.size());
        for (int i = 0; i < mFragments.size(); i++) {
            mFragmentIsDeleted.put(mFragments.get(i), i == position);
        }
        notifyDataSetChanged();
    }

    /**
     * 0 から始まる from,to の位置にある Fragment を削除します。データセットを更新する前にこのメソッドを呼び出してください。
     * notifyDataSetChanged は自動的に呼ばれます。
     * <code>
     * adapter.updateContents(/your contents); // Do not call notifyDataSetChanged
     * adapter.swap(from, to);
     * </code>
     *
     * @param from 0から始まるFragmentの位置
     * @param to 0から始まるFragmentの位置
     */
    public void swapItem(int from, int to) {
        if (mFragments == null || mFragments.isEmpty()) {
            return;
        }
        if (from < 0 || from > mFragments.size()) {
            throwIndexOutOfBoundsException(from);
        }
        if (to < 0 || to > mFragments.size()) {
            throwIndexOutOfBoundsException(to);
        }
        Collections.swap(mFragments, from, to);
        notifyDataSetChanged();
    }

    void throwIndexOutOfBoundsException(int position) {
        throw new IndexOutOfBoundsException("Invalid position " + position + " size is " + mFragments.size());
    }

}