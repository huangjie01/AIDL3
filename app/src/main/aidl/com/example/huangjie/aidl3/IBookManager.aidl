// IBookManager.aidl
package com.example.huangjie.aidl3;

// Declare any non-default types here with import statements

import com.example.huangjie.aidl3.Book;
import com.example.huangjie.aidl3.IBookListener;

interface IBookManager {

   void addBook(in Book book);
   List<Book>  getBookList();
    void registerListener(IBookListener listener);

    void unregisterListener(IBookListener listener);

}