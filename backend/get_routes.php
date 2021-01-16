<?php

include_once "../config/database.php";

$conn = OpenCon();

$language = $_POST['lang'];

GetRoutes($conn, $language);

// Getting all points
function GetRoutes($conn, $language)
{
    $query = GetPreparedSqlQuery($language);
    
    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_ASSOC);

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

function GetPreparedSqlQuery($id)
{
    switch($id)
    {
        case "0":
        return "
        SELECT route.route_category_id AS route_category_id, route.route_name AS route_name, route_chinese.route_alt_name AS route_alt_name, route.id AS route_id
        FROM route
        INNER JOIN route_chinese
        ON route.route_category_id = route_chinese.id
        ";
        case "1": 
        return "
        SELECT route.route_category_id AS route_category_id, route.route_name AS route_name, route_english.route_alt_name AS route_alt_name, route.id AS route_id
        FROM route
        INNER JOIN route_english
        ON route.route_category_id = route_english.id
        ";
    }
}

?>