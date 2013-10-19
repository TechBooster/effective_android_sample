<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools"
  android:layout_width=
    <#if buildApi lt 8 >"fill_parent"<#else>"match_parent"</#if>
  android:layout_height=
    <#if buildApi lt 8 >"fill_parent"<#else>"match_parent"</#if>
  tools:context=".${activityClass}" >

  <ListView
    android:id="@+id/listview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

</LinearLayout>