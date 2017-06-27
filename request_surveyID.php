
<?php 

try
{
	$url = (isset($_SERVER['HTTPS']) ? "https" : "http") . "://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";	
	$parts = parse_url($url);
	parse_str($parts['query'], $query);
	$password = $query['token'];
	
	$response = array();
	
  	$host        = "host=212.237.28.119";
  	$port        = "port=5432";
  	$dbname      = "dbname=limedb";
  	$credentials = "user=limedbadministrator password=1Cisco2";

	$bdd = new PDO('pgsql:host=212.237.28.119; port = 5432; dbname= limedb;', 'limedbadministrator', '1Cisco2');
	$surveyID_in_bdd = 'select * from  surveys_languagesettings';


	$result_surveyID=$bdd->prepare($surveyID_in_bdd );     
	$result_surveyID->execute();                
	$res_surveyID = $result_surveyID->fetchAll();
  

	for($i=0;$i<count($res_surveyID);$i++){  

		$db_table = "tokens_".$res_surveyID[$i]['surveyls_survey_id'];
	
	    $query_password="select token from $db_table where  token = '$password'"; 
	    $result_password=$bdd->prepare($query_password);     
	    $result_password->execute();                
	    $res_password=$result_password->fetchAll();

		if($res_password){

		array_push($response, array("ID"=>$res_surveyID[$i]['surveyls_survey_id'], "title"=>$res_surveyID[$i]['surveyls_title']));
					
			}
		}  
		
		
    $response = json_encode($response); 
    	
	echo $response;	
	}
catch (Exception $e)
{
        die('Erreur : ' . $e->getMessage());
}
?>
