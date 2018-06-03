// IBookListener.aidl
package com.example.huangjie.aidl3;

// Declare any non-default types here with import statements

import com.example.huangjie.aidl3.Book;

interface IBookListener {
   void onBookChange(in Book book);
}
