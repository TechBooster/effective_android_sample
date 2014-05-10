<?php
 
$fp = fopen("id.txt", "a");
fwrite($fp, $_POST['regId']);
fclose($fp);
 
print('save ok');