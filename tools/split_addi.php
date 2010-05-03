#!/usr/bin/php
<?php

$startdir = dirname(realpath($argv[0]));
$inclnk = $startdir . "/inc";


// require_once "$inclnk/minixml-1.3.8/minixml.inc.php";
// require_once "$inclnk/toolkit/toolkit.php";
// require_once "$inclnk/marc_class/marc_class.php";
// require_once "$inclnk/OLS_class_lib/inifile_class.php";
// require_once "$inclnk/OLS_class_lib/oci_class.php";
// require_once "$inclnk/OLS_class_lib/pg_database_class.php";

// require_once "splitXMLfile_class.php";

function usage($str="") 
{
  global $argv, $inifile;
  if ( $str != "" ) {
    echo "-------------------\n";
    echo "\n$str \n";
  }
  
  echo "Usage: php $argv[0]\n";
  echo "\t-i inputfile \n";
  echo "\t-s startrecord (first record = 0) \n";  
  echo "\t-n number of records\n";
  echo "\t-v verbose level\n";
  echo "\t-h help (shows this message)\n";
  exit;
}
  

$options = getopt("?hv:i:n:s:");
if ( array_key_exists('h',$options) ) usage();

$test = $options[v];
$startrecord = 0;
$numberofrecords = 0;

if ( array_key_exists('i',$options) ) $inputfile = $options[i];
if ( array_key_exists('s',$options) ) $startrecord = $options[s];
if ( array_key_exists('n',$options) ) $numberofrecords = $options[n];

//echo "inputfile:$inputfile\n";

$fp = fopen($inputfile,"r");

$numberofrecords = $numberofrecords * 2;
$startrecord = $startrecord * 2;
$count = 0;

while ( $bytecount = fgets($fp) ) {
  $refrecord = fread($fp,$bytecount);
  $rest = fgets($fp);
  $count++;

  if ( $count <= $startrecord ) continue;
 
  echo $bytecount;
  echo $refrecord;
  echo $rest;

  if ( $numberofrecords )
    if ( $count >= $numberofrecords ) break;
}

      
?>