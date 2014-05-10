<?php
require_once "HTTP/Request.php"
 
$regid = "";  // register.phpで取得したID
$apikey = ""; // API Key
 
$rq = new HTTP_Request("https://android.googleapis.com/gcm/send");
$rq->setMethod(HTTP_REQUEST_METHOD_POST);
$rq->addHeader("Authorization", "key=".$apikey);
$rq->addPostData("registration_id", $regid);
$rq->addPostData("collapse_key", "1");
$rq->addPostData("data.message", "posted from gcm");
 
if (!PEAR::isError($rq->sendRequest())) {
    print "\n" . $rq->getResponseBody();
} else {
    print "\nError has occurred";
}