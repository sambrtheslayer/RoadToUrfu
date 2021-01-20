
            <?php

$language = "0";


$conn = OpenCon();

GetRoutes($conn, $language);

// Getting all points
function GetRoutes($conn, $language)
{
    $query = GetPreparedSqlQueryRoutes($language);
    
    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_NUM);

            for($i = 0; $i < count($data); $i++)
            {
                $title = $data[$i][1];
                echo '<a class="dropdown-item" href="#">'.$title.'</a>';
                //echo $data[$i][1]."<br>";
                //print_r(array_count_values($data[$i]));
            }		
        }
    }
    catch(Exception $e)
    {
        echo json_encode(array("message" => $e -> getMessage()), JSON_UNESCAPED_UNICODE);
    }

    CloseCon($conn);
}

function GetPreparedSqlQueryRoutes($id)
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


<?php

$path = "../../../api/config/database.php";

include_once $path;

$conn = OpenCon();

$language = "0";

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
            $data = mysqli_fetch_all($result, MYSQLI_NUM);
            
            //$response_data = json_decode($data);
            for($i = 0; $i < count($data); $i++)
            {
                $title = $data[$i][1];
                echo '<a class="dropdown-item" href="#">'.$title.'</a>';
                //echo $data[$i][1]."<br>";
                //print_r(array_count_values($data[$i]));
            }
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


<div class="container">
        <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              Категории
            </button>            
            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
            <a class="dropdown-item" href="#">111</a>
            </div>
          </div>
    </div>