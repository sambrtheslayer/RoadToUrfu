<?php

include_once "../config/database.php";

$conn = OpenCon();

$language = $_POST['lang'];

GetCategories($conn, $language);

// Getting all points
function GetCategories($conn, $language)
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
        SELECT category.id AS id, category.category_name AS category_name, category_chinese.category_alt_name AS category_alt_name
        FROM category
        INNER JOIN category_chinese
        ON category.id = category_chinese.id
        ";
        case "1": 
        return "
        SELECT category.id AS id, category.category_name AS category_name, category_english.category_alt_name AS category_alt_name
        FROM category
        INNER JOIN category_english
        ON category.id = category_english.id
        ";
    }
}

?>