-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:3306
-- Время создания: Дек 07 2020 г., 09:43
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
(18, '*ИЕНиМ (матмех) на китайском*', '*Описание на китайском*'),
(19, 'Манеж УрФУ на китайском', 'Описание на китайском'),
(20, 'Стадион УрФУ на китайском', 'Описание на китайском'),
(21, 'Баскетбольный зал на китайском', 'Описание на китайском');

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
  `image` text COLLATE utf8_unicode_ci NOT NULL,
  `address` text COLLATE utf8_unicode_ci NOT NULL,
  `contacts` text COLLATE utf8_unicode_ci NOT NULL,
  `site` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `point`
--

INSERT INTO `point` (`id`, `latitude`, `longitude`, `name`, `description`, `category`, `image`, `address`, `contacts`, `site`) VALUES
(0, 56.844028, 60.654068, 'ГУК', 'Это - главный учебный корпус', 0, '', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\\nСб 8:30 – 16:00\\nКонтакт-центр: 8-800-100-50-44', 'https://urfu.ru/ru/'),
(1, 56.8407873, 60.6507781, 'ИРИТ-РТФ', 'Это - радиофак =)', 0, '', 'ул. Мира, 32', 'Пн-пт 08:00 – 22:00\\n+7 (343) 375-97-00', 'https://rtf.urfu.ru/ru/'),
(2, 56.828604, 60.619209, 'ИЕНиМ', 'Это - институт естеств. наук и математики =)', 0, '../../image/point_2.png', 'ул. Тургенева, 4', 'Пн-пт 08:00 – 22:00\\n+7 (343) 389-97-03', 'https://insma.urfu.ru/'),
(3, 56.840362, 60.616244, 'УГИ', 'Это - университет гуманитариев =)', 0, '', 'пр. Ленина, 51', 'Пн-пт 08:00 – 22:00\\n+7 (343) 389-94-12', 'https://urgi.urfu.ru/ru/'),
(5, 56.843013, 60.652508, 'ВШЭМ', 'Это - высшая школа экономики и менеджмента', 0, '', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\\nСб 8:30 – 16:00\\nКонтакт-центр: 8-800-100-50-44', 'https://urfu.ru/ru/'),
(6, 56.844614, 60.652247, 'Электрофак', 'Это - энергитический институт', 0, '', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\\nСб 8:30 – 16:00\\nКонтакт-центр: 8-800-100-50-44', 'https://urfu.ru/ru/'),
(7, 56.844769, 60.65401, 'Мехмаш', 'Это - институт механики и машиностроения', 0, '', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\\nСб 8:30 – 16:00\\nКонтакт-центр: 8-800-100-50-44', 'https://urfu.ru/ru/'),
(8, 56.844934, 60.650475, 'ИСА', 'Это - институт строительства и архитектуры', 0, '', 'ул. Мира, 17', 'Пн-пт 08:00 – 22:00\\n+7 (343) 375-44-70', 'https://sti.urfu.ru/ru/home/'),
(9, 56.845003, 60.651894, 'ИСА Пр', 'Это - пристройка ИСА', 0, '', 'ул. Мира, 17А', 'Пн-пт 08:00 – 22:00', 'https://sti.urfu.ru/ru/home/'),
(10, 56.842709, 60.651691, 'ФТИ', 'Физтех лучше всех', 0, '', 'ул. Мира, 21', 'Пн-пт 08:00 – 22:00\\n+7 (343) 375-41-51', 'https://fizteh.urfu.ru/ru/'),
(11, 56.842849, 60.655404, 'УралЭНИН (Т)', 'УралЭНИН - оп', 0, '', 'ул. Софьи Ковалевской, 5', 'Пн-пт 08:00 – 22:00\\n+7 (343) 375-41-87', 'https://enin.urfu.ru/ru/'),
(12, 56.842516, 60.658542, 'ВТОиБ', 'Какая-то шляпа', 0, '', 'ул. Комсомольская, 62', 'Пн-пт 08:00–17:15\\n+7 (343) 374-50-23', 'https://vuc.urfu.ru/ru/'),
(13, 56.8421, 60.64834, 'ХТИ', 'Это - химико-технологический институт', 0, '', 'ул. Мира, 28', 'Пн-пт 08:00 – 22:00\\nТелефон: +7 (343) 374-39-05', 'https://hti.urfu.ru/ru/'),
(14, 56.842191918, 60.6498735, 'ИНМиТ', 'Это - институт новых материалов и технологий', 0, '', 'ул. Мира, 28', 'Пн-пт 08:00 – 22:00\\nТелефон: +7 (343) 375-44-39', 'https://inmt.urfu.ru/ru/'),
(15, 56.81715, 60.613278, 'ИнЭУ', 'Это - институт экономики и управления', 0, '', 'ул. Гоголя, 25', 'Пн-пт 08:00 – 22:00\\n8-800-234-95-56, +7 (343) 371-10-03', 'https://gsem.urfu.ru/ru/'),
(16, 56.83174850995216, 60.61083336021693, 'ВШЭМ', 'Это - высшая школа экономики и менеджмента', 0, '', '', '', ''),
(17, 56.837221, 60.590116, 'ИнЭУ', 'Это - институт экономики и управления', 0, '', 'пр. Ленина, 13Б', 'Пн-пт 08:00 – 22:00\\n8-800-234-95-56, +7 (343) 371-10-03', 'https://gsem.urfu.ru/ru/'),
(18, 56.8411, 60.614753, 'ИЕНиМ (матмех)', 'Это - институт экономики и управления', 0, '', 'ул. Куйбышева, 48', 'Пн-пт 08:00 – 22:00\\n+7 (343) 389-97-03', 'https://insma.urfu.ru/'),
(19, 56.838903, 60.65373, 'Манеж УрФУ', 'Легкая атлетика, аэробика, скалолазание', 1, '', 'ул. Мира, 29\\n1 вход с улицы Мира\\n2 вход с улицы Коминтерна', '', ''),
(20, 56.838377, 60.654263, 'Стадион УрФУ', 'Легкая атлетика, футбол, мини-футбол, ориентирование и спортивный туризм Зимой: лыжные гонки', 1, '', 'Вход через манеж с ул. Мира', '', ''),
(21, 56.842286, 60.655622, 'Баскетбольный зал', 'Баскетбол', 1, '', 'ул. Софьи Ковалевской, 5\\nВход через учебный корпус УралЭНИН', '', '');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

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
