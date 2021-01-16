<?php

/*
!
Пересмотреть файл
!
*/

include_once "../config/database.php";

$conn = OpenCon();

if(!isset($_POST['category']))
{
    GetAllPoints($conn);
}

$language = $_POST['lang'];

$given_category = $_POST['category'];

GetPoints($conn, $given_category, $language);

// Getting all points
function GetPoints($conn, $given_category, $language)
{
    $query = GetPreparedSqlQuery($language, $given_category);

    /*echo $given_category;
    echo "\n";
    echo $language;*/
    
    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_ASSOC);

            /*
            point_id
            point_name
            point_alt_name
            point_latitude
            point_longitude
            point_classroom
            point_description
            point_alt_description
            */

            header('Content-Type: application/json');
            
            echo json_encode($data);				
        }
    }
    catch(Exception $e)
    {
        echo json_encode(array("message" => $e -> getMessage()), JSON_UNESCAPED_UNICODE);
    }

    CloseCon($conn);
}

function GetPreparedSqlQuery($id, $given_category)
{
    switch($id)
    {
        case "0":
        return "
            SELECT point.id AS point_id,
            point.name AS point_name,
            data_chinese.name AS point_alt_name,
            point.latitude AS point_latitude,
            point.longitude AS point_longitude,
            point.classroom AS point_classroom,
            point.description AS point_description,
            data_chinese.description AS point_alt_description,
            point.address AS point_address,
            point.contacts AS point_contacts,
            point.site AS point_site
            FROM point
            INNER JOIN data_chinese
            ON point.id = data_chinese.id
            WHERE point.category = '$given_category'
        ";
        case "1": 
            return "
            SELECT point.id AS point_id,
            point.name AS point_name,
            data_english.name AS point_alt_name,
            point.latitude AS point_latitude,
            point.longitude AS point_longitude,
            point.classroom AS point_classroom,
            point.description AS point_description,
            data_english.description AS point_alt_description,
            point.address AS point_address,
            point.contacts AS point_contacts,
            point.site AS point_site
            FROM point
            INNER JOIN data_english
            ON point.id = data_english.id
            WHERE point.category = '$given_category'
        ";
    }
}

function GetAllPoints($conn)
{
    $query  = "
    SELECT point.id AS point_id,
        point.name AS point_name,
        data_chinese.name AS point_alt_name,
        point.latitude AS point_latitude,
        point.longitude AS point_longitude,
        point.classroom AS point_classroom,
        point.description AS point_description,
        data_chinese.description AS point_alt_description,
        point.address AS point_address,
        point.contacts AS point_contacts,
        point.site AS point_site
    FROM point
    INNER JOIN data_chinese
    ON point.id = data_chinese.id
    ";
    
    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_ASSOC);

            /*
            point_id
            point_name
            point_alt_name
            point_latitude
            point_longitude
            point_image
            point_description
            point_alt_description
            */

            header('Content-Type: application/json');
            
            echo json_encode($data);				
        }
    }
    catch(Exception $e)
    {
        echo json_encode(array("message" => $e -> getMessage()), JSON_UNESCAPED_UNICODE);
    }

    CloseCon($conn);
}

?>