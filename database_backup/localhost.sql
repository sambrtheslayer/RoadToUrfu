-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:3306
-- Время создания: Ноя 26 2020 г., 09:42
-- Версия сервера: 10.3.16-MariaDB
-- Версия PHP: 7.3.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `id15425196_world`
--
CREATE DATABASE IF NOT EXISTS `id15425196_world` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `id15425196_world`;

-- --------------------------------------------------------

--
-- Структура таблицы `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `category_name` text COLLATE utf8_unicode_ci NOT NULL,
  `category_alt_name` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `category`
--

INSERT INTO `category` (`id`, `category_name`, `category_alt_name`) VALUES
(0, 'Учебные корпуса', '育大楼'),
(1, 'Спортивные объекты', '体育设施'),
(2, 'Общежития', '宿舍'),
(3, 'МСЧ', '大学医院');

-- --------------------------------------------------------

--
-- Структура таблицы `category_chinese`
--

CREATE TABLE `category_chinese` (
  `id` int(11) NOT NULL,
  `category_alt_name` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `data_chinese`
--

CREATE TABLE `data_chinese` (
  `id` int(11) NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `data_chinese`
--

INSERT INTO `data_chinese` (`id`, `name`, `description`) VALUES
(0, '主教学楼', 'Это - главный учебный корпус (эту фразу переписать на китайский)'),
(1, '无线电电子与信息技术研究所', '*китайское описание*'),
(2, '自然科学与数学研究所', '*китайское описание*'),
(3, '拉尔人文研究所', '*китайское описание*'),
(4, 'Ниси Ниси Ниша Ниша', 'Тут описание на китайском'),
(5, '*китайское название*', '*описание на китайском*'),
(6, '*Электрофак на китайском*', '*Описание на китайском*'),
(7, '*Мехмаш на китайском*', '*Описание на китайском*'),
(8, '*ИСА на китайском*', '*Описание на китайском*'),
(9, '*ИСА Пр на китайском*', '*Описание на китайском*'),
(10, '*ФТИ на китайском*', '*Описание на китайском*'),
(11, '*УралЭНИН (Т) на китайском*', '*Описание на китайском*'),
(12, '*ВТОиБ на китайском*', '*Описание на китайском*'),
(13, '*ХТИ на китайском*', '*Описание на китайском*'),
(14, '*ИНМиТ на китайском*', '*Описание на китайском*');

-- --------------------------------------------------------

--
-- Структура таблицы `point`
--

CREATE TABLE `point` (
  `id` int(11) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `alt_description` int(11) NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `alt_name` int(11) NOT NULL,
  `category` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `point`
--

INSERT INTO `point` (`id`, `latitude`, `longitude`, `name`, `alt_description`, `description`, `alt_name`, `category`) VALUES
(0, 56.843486, 60.653813, 'ГУК', 0, 'Это - главный учебный корпус', 0, 0),
(1, 56.8407873, 60.6507781, 'ИРИТ-РТФ', 1, 'Это - радиофак =)', 1, 0),
(2, 666, 666, 'ИЕНиМ', 2, 'Это - институт естеств. наук и математики =)', 2, 0),
(3, 666, 666, 'УГИ', 3, 'Это - университет гуманитариев =)', 3, 0),
(4, 666, 666, 'Общежитие №1', 4, 'Это - общежитие №1 =)', 4, 2),
(5, 56.84337, 60.6529777, 'ВШЭМ', 5, 'Это - высшая школа экономики и менеджмента', 5, 0),
(6, 56.8444195, 60.652405, 'Электрофак', 6, 'Это - энергитический институт', 6, 0),
(7, 56.844473, 60.653, 'Мехмаш', 7, 'Это - институт механики и машиностроения', 7, 0),
(8, 56.8444693, 60.6509883, 'ИСА', 8, 'Это - институт строительства и архитектуры', 8, 0),
(9, 56.8444693, 60.6509883, 'ИСА Пр', 9, 'Это - пристройка ИСА', 9, 0),
(10, 56.8427831, 60.6509658, 'ФТИ', 10, 'Физтех лучше всех', 10, 0),
(11, 56.8427831, 60.6542332, 'УралЭНИН (Т)', 11, 'УралЭНИН - оп', 11, 0),
(12, 56.8422478, 60.6581714, 'ВТОиБ', 12, 'Какая-то шляпа', 12, 0),
(13, 56.842305, 60.6584602, 'ХТИ', 13, 'Это - химико-технологический институт', 13, 0),
(14, 56.8423134, 60.6489432, 'ИНМиТ', 14, 'Это - институт новых материалов и технологий', 14, 0);

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `data_chinese`
--
ALTER TABLE `data_chinese`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `point`
--
ALTER TABLE `point`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT для таблицы `point`
--
ALTER TABLE `point`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
