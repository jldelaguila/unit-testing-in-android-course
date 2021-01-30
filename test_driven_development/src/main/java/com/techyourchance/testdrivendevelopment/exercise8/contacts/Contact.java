package com.techyourchance.testdrivendevelopment.exercise8.contacts;

import java.util.Objects;

public class Contact {

    private final String mId;
    private final String mFullName;
    private final String mImageUrl;

    public Contact(String id, String fullName, String imageUrl) {
        mId = id;
        mFullName = fullName;
        mImageUrl = imageUrl;
    }

    public String getId() {
        return mId;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return mId.equals(contact.mId) &&
                mFullName.equals(contact.mFullName) &&
                mImageUrl.equals(contact.mImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mFullName, mImageUrl);
    }
}
