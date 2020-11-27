-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:3306
-- Время создания: Ноя 27 2020 г., 11:25
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
  `category_name` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `category`
--

INSERT INTO `category` (`id`, `category_name`) VALUES
(0, 'Учебные корпуса'),
(1, 'Спортивные объекты'),
(2, 'Общежития'),
(3, 'МСЧ');

-- --------------------------------------------------------

--
-- Структура таблицы `category_chinese`
--

CREATE TABLE `category_chinese` (
  `id` int(11) NOT NULL,
  `category_alt_name` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `category_chinese`
--

INSERT INTO `category_chinese` (`id`, `category_alt_name`) VALUES
(0, '育大楼'),
(1, '体育设施'),
(2, '宿舍'),
(3, '大学医院');

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
(5, '*китайское название*', '*описание на китайском*'),
(6, '*Электрофак на китайском*', '*Описание на китайском*'),
(7, '*Мехмаш на китайском*', '*Описание на китайском*'),
(8, '*ИСА на китайском*', '*Описание на китайском*'),
(9, '*ИСА Пр на китайском*', '*Описание на китайском*'),
(10, '*ФТИ на китайском*', '*Описание на китайском*'),
(11, '*УралЭНИН (Т) на китайском*', '*Описание на китайском*'),
(12, '*ВТОиБ на китайском*', '*Описание на китайском*'),
(13, '*ХТИ на китайском*', '*Описание на китайском*'),
(14, '*ИНМиТ на китайском*', '*Описание на китайском*'),
(15, '*ИнЭУ на китайском*', '*Описание на китайском*'),
(16, '*ВШЭМ на китайском*', '*Описание на китайском*'),
(17, '*ИнЭУ на китайском*', '*Описание на китайском*'),
(18, '*ИЕНиМ (матмех) на китайском*', '*Описание на китайском*');

-- --------------------------------------------------------

--
-- Структура таблицы `point`
--

CREATE TABLE `point` (
  `id` int(11) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `category` int(11) NOT NULL,
  `image` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `point`
--

INSERT INTO `point` (`id`, `latitude`, `longitude`, `name`, `description`, `category`, `image`) VALUES
(0, 56.843486, 60.653813, 'ГУК', 'Это - главный учебный корпус', 0, ''),
(1, 56.8407873, 60.6507781, 'ИРИТ-РТФ', 'Это - радиофак =)', 0, ''),
(2, 56.8280325, 60.617249, 'ИЕНиМ', 'Это - институт естеств. наук и математики =)', 0, '../../image/point_2.png'),
(3, 56.8404446, 60.6137215, 'УГИ', 'Это - университет гуманитариев =)', 0, ''),
(5, 56.84337, 60.6529777, 'ВШЭМ', 'Это - высшая школа экономики и менеджмента', 0, ''),
(6, 56.8444195, 60.652405, 'Электрофак', 'Это - энергитический институт', 0, ''),
(7, 56.844473, 60.653, 'Мехмаш', 'Это - институт механики и машиностроения', 0, ''),
(8, 56.8444693, 60.6509883, 'ИСА', 'Это - институт строительства и архитектуры', 0, ''),
(9, 56.8444693, 60.6509883, 'ИСА Пр', 'Это - пристройка ИСА', 0, ''),
(10, 56.8427831, 60.6509658, 'ФТИ', 'Физтех лучше всех', 0, ''),
(11, 56.8427831, 60.6542332, 'УралЭНИН (Т)', 'УралЭНИН - оп', 0, ''),
(12, 56.8422478, 60.6581714, 'ВТОиБ', 'Какая-то шляпа', 0, ''),
(13, 56.842305, 60.6584602, 'ХТИ', 'Это - химико-технологический институт', 0, ''),
(14, 56.8423134, 60.6489432, 'ИНМиТ', 'Это - институт новых материалов и технологий', 0, ''),
(15, 56.8172067, 60.6134809, 'ИнЭУ', 'Это - институт экономики и управления', 0, ''),
(16, 56.8317602, 60.6087875, 'ВШЭМ', 'Это - высшая школа экономики и менеджмента', 0, ''),
(17, 56.8373599, 60.5880323, 'ИнЭУ', 'Это - институт экономики и управления', 0, ''),
(18, 56.8407304, 60.614028, 'ИЕНиМ (матмех)', 'Это - институт экономики и управления', 0, '');

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `category_chinese`
--
ALTER TABLE `category_chinese`
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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `category_chinese`
--
ALTER TABLE `category_chinese`
  ADD CONSTRAINT `category_chinese_ibfk_1` FOREIGN KEY (`id`) REFERENCES `category` (`id`);

--
-- Ограничения внешнего ключа таблицы `data_chinese`
--
ALTER TABLE `data_chinese`
  ADD CONSTRAINT `data_chinese_ibfk_1` FOREIGN KEY (`id`) REFERENCES `point` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
