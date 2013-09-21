<?xml version="1.0"?>
<recipe>
    <merge from="AndroidManifest.xml.ftl"
             to="${manifestOut}/AndroidManifest.xml" />

    <merge from="res/values/strings.xml.ftl"
             to="${resOut}/values/strings.xml" />

    <instantiate from="res/layout/activity_simple.xml.ftl"
                   to="${resOut}/layout/${layoutName}.xml" />
    
    <instantiate from="src/app_package/SimpleActivity.java.ftl"
                   to="${srcOut}/${activityClass}.java" />

    <open file="${resOut}/layout/${layoutName}.xml" />
</recipe>
