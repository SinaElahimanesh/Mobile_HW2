package edu.sharif.mobile_hw2;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Bookmark {
    private static ArrayList<Bookmark> bookmarks = new ArrayList<>();
    private String name;
    private double latLong;
    private double latLat;
    private int ID = 0;

//    static {
//       bookmarks.add(new Bookmark("title", 12, 34));
//        bookmarks.add(new Bookmark("tehran", 56, 65));
//        bookmarks.add(new Bookmark("tehran2", 56, 65));
//        bookmarks.add(new Bookmark("tehran3", 56, 65));
//        bookmarks.add(new Bookmark("tehran4", 56, 65));
//        bookmarks.add(new Bookmark("tehran5", 56, 65));
//    }

    public Bookmark(String name, double latLong, double latLat) {
        this.name = name;
        this.latLat = latLat;
        this.latLong = latLong;
    }

    public String getName() {
        return name;
    }

    public double getLatLong() {
        return latLong;
    }

    public double getLatLat() {
        return latLat;
    }

    public int getID() {
        return ID;
    }

    public static void deleteBookmarks(){
        bookmarks.clear();
    }

    public static void addBookmark(Bookmark bookmark) throws Exception{
        for (Bookmark bookmarkList : bookmarks) {
            if(bookmarkList.getName().equals(bookmark.getName()))
                throw new Exception("A bookmark with this name already exists. Please choose another name.");
        }
        bookmarks.add(bookmark);
    }

    public static void addAllBookmarks(List<Bookmark> bookmarkList){
        bookmarks.addAll(bookmarkList);
    }

    public static ArrayList<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public static void deleteBookmark(Bookmark bookmark) throws Exception{
        if(bookmarks.contains(bookmark)){
            bookmarks.remove(bookmark);
        }else{
            throw new Exception("No bookmarks were found");
        }
    }

    public static void setBookmarks(ArrayList<Bookmark> bookmarks) {
        Bookmark.bookmarks = bookmarks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatLong(double latLong) {
        this.latLong = latLong;
    }

    public void setLatLat(double latLat) {
        this.latLat = latLat;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
