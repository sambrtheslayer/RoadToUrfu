-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Хост: 10.19.5.15:3306
-- Время создания: Янв 18 2021 г., 14:02
-- Версия сервера: 10.2.31-MariaDB-10.2.31+maria~stretch-log
-- Версия PHP: 5.6.30-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `c21535_roadtourfu_ai_info_ru`
--
CREATE DATABASE IF NOT EXISTS `c21535_roadtourfu_ai_info_ru` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `c21535_roadtourfu_ai_info_ru`;

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
(0, '教学楼'),
(1, '体育设施'),
(2, '宿舍'),
(3, '医疗卫生部门');

-- --------------------------------------------------------

--
-- Структура таблицы `category_english`
--

CREATE TABLE `category_english` (
  `id` int(11) NOT NULL,
  `category_alt_name` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `category_english`
--

INSERT INTO `category_english` (`id`, `category_alt_name`) VALUES
(0, 'Educational buildings'),
(1, 'Sports buildings'),
(2, 'Dorms'),
(3, 'Medical and sanitary building');

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
(0, '主要的教学楼', 'Это - главный учебный корпус (эту фразу переписать на китайский)'),
(1, '无线电电子与信息技术研究所', '*китайское описание*'),
(2, '自然科学与数学研究所', '*китайское описание*'),
(3, '乌拉尔人道主义研究所', '*китайское описание*'),
(5, '经济管理学院', '*описание на китайском*'),
(6, '乌拉尔电力工程学院', '*Описание на китайском*'),
(7, '机械工程学院', '*Описание на китайском*'),
(8, '建筑学院', '*Описание на китайском*'),
(9, '建筑学院', '*Описание на китайском*'),
(10, '物理技术研究所', '*Описание на китайском*'),
(11, '乌拉尔电力工程学院', '*Описание на китайском*'),
(12, '军事技术教育与安全研究所', '*Описание на китайском*'),
(13, '化学技术研究所', '*Описание на китайском*'),
(14, '新材料技术研究所', '*Описание на китайском*'),
(15, '经济管理学院', '*Описание на китайском*'),
(16, '经济管理学院', '*Описание на китайском*'),
(17, '经济管理学院', '*Описание на китайском*'),
(18, '自然科学与数学研究所', '*Описание на китайском*'),
(19, '室内运动场', 'Описание на китайском'),
(20, '体育场', 'Описание на китайском'),
(21, '篮球馆', 'Описание на китайском'),
(22, '拳击举重馆', 'Описание на китайском'),
(23, '游泳池', 'Описание на китайском'),
(24, '综合性体育馆', 'Описание на китайском'),
(25, '商业中心', 'Описание на китайском'),
(26, '学生中心«明星的»', 'Описание на китайском'),
(27, '体育和娱乐中心', 'Описание на китайском'),
(28, '迷你足球场', 'Описание на китайском'),
(29, '“太平”健身房', 'Описание на китайском'),
(30, '宿舍 №1', 'Описание на китайском'),
(31, '宿舍 №2', 'Описание на китайском'),
(32, '宿舍 №3', ' Описание на китайском'),
(33, '宿舍 №4', 'Описание на китайском'),
(34, '宿舍 №5', 'Описание на китайском'),
(35, '宿舍 №6', 'Описание на китайском'),
(36, '宿舍 №7', 'Описание на китайском'),
(37, '宿舍 №8', 'Описание на китайском'),
(38, '宿舍 №10', 'Описание на китайском'),
(39, '宿舍 №11', 'Описание на китайском'),
(40, '宿舍 №12', 'Описание на китайском'),
(41, '宿舍 №13', 'Описание на китайском'),
(42, '宿舍 №14', 'Описание на китайском'),
(43, '宿舍 №15', 'Описание на китайском'),
(44, '宿舍 №16', 'Описание на китайском'),
(45, '医疗卫生部门', 'Описание на китайском'),
(47, 'S.M.纪念碑 基洛夫', '这座纪念碑于70年代出现在UPI附近，并立即成为学生“在后备箱”的聚会场所（出于某种原因，靴子出现在基洛夫同志制服中最杰出的学生身上）。 城市假期经常在这里举行音乐会。'),
(48, '伟大卫国战争参与者的纪念碑', '参加大爱国战争的UPI的学生，老师和工作人员的纪念碑。 基座上的铭文写着：“对1941-1945年卫国战争英雄的记忆不会消逝几个世纪。来自以S.M. Kirov命名的乌拉尔工业学院的Komsomol成员。”'),
(49, '足球运动员纪念碑', '三米高的足球运动员的身材是用不锈钢切割而成的，并逐字地组装在一起。 他们致力于举办该市最大的体育赛事-2018 FIFA世界杯'),
(50, 'Ya.M.纪念碑 斯维尔德洛夫', '斯维尔德洛夫·雅科夫·米哈伊洛维奇-苏联政权的杰出政治人物之一。 它于1927年开业。 该项目的作者是雕塑家Sigismund Dobrovsky，建筑师Matvey Kharlamov。 雅科夫·斯维尔德洛夫（Yakov Sverdlov）处于全面成长的状态，他的手被动态地放在一边。 这种姿势是布尔什维克在公开演讲中的典型代表。 这座雕塑高3.93米，高5.2米。 它被认为是该城市最知名的建筑之一。'),
(51, 'A.S.纪念碑 波波夫', '这座纪念碑是献给广播电台创始人亚历山大·斯蒂潘诺维奇·波波夫（Alexander Stepanovich Popov）的。 它于1975年开业。 它位于城市中心，毗邻亚历山大·史塔潘诺维奇（Alexander Stepanovich）就读的神学学校。 纪念碑的作者是雕塑家V.E. Egorov和建筑师P.D. Demintsev。 2005年，纪念碑被修复并涂成青铜色。 同年，他被叶卡捷琳堡大主教和Verkhoturye大主教奉献。');

-- --------------------------------------------------------

--
-- Структура таблицы `data_english`
--

CREATE TABLE `data_english` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL,
  `description` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `data_english`
--

INSERT INTO `data_english` (`id`, `name`, `description`) VALUES
(0, 'The main academic building', 'It\'s the GUK'),
(1, 'Institute of Radio Electronics and Information Technologies', 'It\'s the irit-rtf'),
(2, 'Institute of Natural Sciences and Mathematics', 'It\'s the ienim'),
(3, 'Ural Institute for the Humanities', 'It\'s the uhi'),
(5, 'Institute of Economics and Management', 'It\'s the iem'),
(6, 'Ural Power Engineering Institute', 'It\'s the iem'),
(7, 'Mechanical Engineering Institute', 'It\'s the iem'),
(8, 'Institute of Construction and Architecture', 'It\'s the iem'),
(9, 'Institute of Construction and Architecture (annex)', 'It\'s the iem'),
(10, 'Institute of Physics and Technology', 'It\'s the iem'),
(11, 'Ural Power Engineering Institute', 'It\'s the iem'),
(12, 'Institute of Military-Technical Education and Security', 'It\'s the iem'),
(13, 'Institute of Chemical Technology', 'It\'s the iem'),
(14, 'Institute of New Materials and Technologies', 'It\'s the iem'),
(15, 'Institute of Economics and Management', 'It\'s the iem'),
(16, 'Institute of Economics and Management', 'It\'s the iem'),
(17, 'Institute of Economics and Management', 'It\'s the iem'),
(18, 'Institute of Natural Sciences and Mathematics', 'It\'s the iem'),
(19, 'Manege of UrFU', 'It\'s the iem'),
(20, 'UrFU Stadium', 'It\'s the iem'),
(21, 'Basketball Hall', 'It\'s the iem'),
(22, 'Hall of the Boxing and weightlifting', 'It\'s the iem'),
(23, 'UrFU swimming pool', 'It\'s the iem'),
(24, 'Sports complex of game sports', 'It\'s the iem'),
(25, 'B. N. Yeltsin Business Center', 'It\'s the iem'),
(26, 'Student Center \"Zvezdny\"', 'It\'s the iem'),
(27, 'Sports and recreation complex', 'It\'s the iem'),
(28, 'Mini football field', 'It\'s the iem'),
(29, 'Gym \" Taiping\"', 'It\'s the iem'),
(30, 'Dorm №1', 'It\'s the iem'),
(31, 'Dorm №2', 'It\'s the iem'),
(32, 'Dorm №3', 'It\'s the iem'),
(33, 'Dorm №4', 'It\'s the iem'),
(34, 'Dorm №5', 'It\'s the iem'),
(35, 'Dorm №6', 'It\'s the iem'),
(36, 'Dorm №7', 'It\'s the iem'),
(37, 'Dorm №8', 'It\'s the iem'),
(38, 'Dorm №10', 'It\'s the iem'),
(39, 'Dorm №11', 'It\'s the iem'),
(40, 'Dorm №12', 'It\'s the iem'),
(41, 'Dorm №13', 'It\'s the iem'),
(42, 'Dorm №14', 'It\'s the iem'),
(43, 'Dorm №15', 'It\'s the iem'),
(44, 'Dorm №16', 'It\'s the iem'),
(45, 'Medical and sanitary building', 'It\'s the iem'),
(47, 'Monument of the S.M. Kirov', 'The monument appeared near the UPI in the 70s and immediately became a meeting place for students \"At the boot\" (for some reason, boots appeared to the most remarkable students in the uniform of Comrade Kirov). Concerts are often held here during city holidays.'),
(48, 'Monument to the Participants of the Great Patriotic War', 'Monument to the students, teachers and staff of the UPI who participated in the Great Patriotic War. The inscription on the pedestal reads: \"The memory of the heroes of the Great Patriotic War of 1941 - 1945 will not erase the centuries. From the Komsomol members of the Ural Polytechnic Institute named after S. M. Kirov.\"'),
(49, 'Monument to football players', 'Three-meter-high figures of football players were cut out of stainless steel and assembled literally piece by piece. They are dedicated to the largest sports event in the city - the 2018 FIFA World Cup'),
(50, 'Monument of the Ya.M. Sverdlov', 'Sverdlov Yakov Mikhailovich - one of the prominent political figures of Soviet power. It was opened in 1927. The author of the project is sculptor Sigismund Dobrovsky, architect Matvey Kharlamov. Yakov Sverdlov stands in full growth, and his hand is dynamically laid to the side. This posture was typical of the Bolsheviks during public speeches. The sculpture is 3.93 meters high and stands on a pedestal 5.2 meters. It is considered one of the most recognizable compositions of the city.'),
(51, 'Monument to A.S. Popov', 'The monument is dedicated to Alexander Stepanovich Popov, the founder of radio. It was opened in 1975. It is located in the center of the city, next to the Theological School, where Alexander Stepanovich studied. The author of the monument is sculptor V.E. Egorov and architect P.D.Demintsev. In 2005, the monument was restored and painted in bronze. In the same year he was consecrated by the Yekaterinburg and Verkhoturye archbishop.'),
(52, 'Название здания (английский)', 'english description');

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
  `classroom` text COLLATE utf8_unicode_ci NOT NULL,
  `address` text COLLATE utf8_unicode_ci NOT NULL,
  `contacts` text COLLATE utf8_unicode_ci NOT NULL,
  `site` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `point`
--

INSERT INTO `point` (`id`, `latitude`, `longitude`, `name`, `description`, `category`, `classroom`, `address`, `contacts`, `site`) VALUES
(0, 56.844028, 60.654068, 'ГУК', 'Это - главный учебный корпус', 0, 'гук', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\r\nСб 8:30 – 16:00\r\nКонтакт-центр (联系电话): 8-800-100-50-44', 'https://urfu.ru/ru/'),
(1, 56.8407873, 60.6507781, 'ИРИТ-РТФ', 'Это - радиофак ', 0, 'р', 'ул. Мира, 32', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 375-97-00', 'https://rtf.urfu.ru/ru/'),
(2, 56.828604, 60.619209, 'ИЕНиМ', 'Это - институт естеств. наук и математики ', 0, '', 'ул. Тургенева, 4', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 389-97-03', 'https://insma.urfu.ru/'),
(3, 56.840362, 60.616244, 'УГИ', 'Это - университет гуманитариев ', 0, '', 'пр. Ленина, 51', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 389-94-12', 'https://urgi.urfu.ru/ru/'),
(5, 56.843064, 60.652666, 'ИнЭУ(И)', 'Это - высшая школа экономики и менеджмента', 0, 'и', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\r\nСб 8:30 – 16:00\r\nКонтакт-центр (联系电话): 8-800-100-50-44', 'https://urfu.ru/ru/'),
(6, 56.844614, 60.652247, 'Электрофак', 'Это - энергетический институт', 0, 'э', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\r\nСб 8:30 – 16:00\r\nКонтакт-центр (联系电话): 8-800-100-50-44', 'https://urfu.ru/ru/'),
(7, 56.844769, 60.65401, 'Мехмаш', 'Это - институт механики и машиностроения', 0, 'м', 'ул. Мира, 19', 'Пн-пт 08:30 – 19:00\r\nСб 8:30 – 16:00\r\nКонтакт-центр (联系电话): 8-800-100-50-44', 'https://urfu.ru/ru/'),
(8, 56.84505, 60.650818, 'ИСА', 'Это - институт строительства и архитектуры', 0, '', 'ул. Мира, 17', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 375-44-70', 'https://sti.urfu.ru/ru/home/'),
(9, 56.845003, 60.651894, 'ИСА Пр', 'Это - пристройка ИСА', 0, '', 'ул. Мира, 17А', 'Пн-пт 08:00 – 22:00', 'https://sti.urfu.ru/ru/home/'),
(10, 56.842562, 60.65173, 'ФТИ', 'Физтех лучше всех', 0, '', 'ул. Мира, 21', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 375-41-51', 'https://fizteh.urfu.ru/ru/'),
(11, 56.842816, 60.655252, 'УралЭНИН (Т)', 'УралЭНИН - оп', 0, 'т', 'ул. Софьи Ковалевской, 5', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 375-41-87', 'https://enin.urfu.ru/ru/'),
(12, 56.842516, 60.658542, 'ВТОиБ', 'Описание', 0, '', 'ул. Комсомольская, 62', 'Пн-пт 08:00–17:15\r\n+7 (343) 374-50-23', 'https://vuc.urfu.ru/ru/'),
(13, 56.842146, 60.64872, 'ХТИ', 'Это - химико-технологический институт', 0, 'хт', 'ул. Мира, 28', 'Пн-пт 08:00 – 22:00\r\nТелефон (联系电话): +7 (343) 374-39-05', 'https://hti.urfu.ru/ru/'),
(14, 56.842241, 60.649469, 'ИНМиТ', 'Это - институт новых материалов и технологий', 0, 'мт', 'ул. Мира, 28', 'Пн-пт 08:00 – 22:00\r\nТелефон (联系电话): +7 (343) 375-44-39', 'https://inmt.urfu.ru/ru/'),
(15, 56.831831, 60.610991, 'ИнЭУ(И2)', 'Это - институт экономики и управления', 0, '', 'ул. Гоголя, 25', 'Пн-пт 08:00 – 22:00\r\n8-800-234-95-56, +7 (343) 371-10-03', 'https://gsem.urfu.ru/ru/'),
(16, 56.81715, 60.613278, 'ИнЭУ(Ча)', 'Это - высшая школа экономики и менеджмента', 0, '', 'ул. Чапаева, 16', '+7 (343) 375‒44‒44\r\nединая справочная - пн-пт 8:30-19:00; сб 10:00-16:00\r\n8‒800‒100‒50‒44\r\nединая справочная - пн-пт 8:30-19:00; сб 10:00-16:00\r\n+7 (343) 295‒12‒56', 'https://gsem.urfu.ru/ru/'),
(17, 56.837343, 60.590261, 'ИнЭУ(центр)', 'Это - институт экономики и управления', 0, '', 'пр. Ленина, 13Б', 'Пн-пт 08:00 – 22:00\r\n8-800-234-95-56, +7 (343) 371-10-03', 'https://gsem.urfu.ru/ru/'),
(18, 56.841084, 60.614937, 'ИЕНиМ (матмех)', 'Это - институт естественных наук и математики', 0, '', 'ул. Куйбышева, 48', 'Пн-пт 08:00 – 22:00\r\n+7 (343) 389-97-03', 'https://insma.urfu.ru/'),
(19, 56.838903, 60.65373, 'Манеж УрФУ', 'Легкая атлетика, аэробика, скалолазание', 1, '', 'ул. Мира, 29\r\n1 вход с улицы Мира\r\n2 вход с улицы Коминтерна', '', ''),
(20, 56.838377, 60.654263, 'Стадион УрФУ', 'Легкая атлетика, футбол, мини-футбол, ориентирование и спортивный туризм Зимой: лыжные гонки', 1, '', 'Вход через манеж с ул. Мира', '', ''),
(21, 56.842286, 60.655622, 'Баскетбольный зал', 'Баскетбол', 1, '', 'ул. Софьи Ковалевской, 5\r\nВход через учебный корпус УралЭНИН', '', ''),
(22, 56.839353, 60.657737, 'Зал бокса и тяжелой атлетики', 'Бокс, тяжелая атлетика', 1, '', 'ул. Малышева, 138А', '', ''),
(23, 56.83726, 60.655225, 'Бассейн УрФУ', 'Плавание', 1, '', 'ул. Коминтерна, 14а', '+7 343 375-93-84', ''),
(24, 56.837626, 60.654901, 'СКИВС', 'Баскетбол, мини-футбол, волейбол, гандбол', 1, '', 'ул. Коминтерна, 14', '', ''),
(25, 56.836584, 60.654922, 'Бизнес-центр им. Б.Н. Ельцина', 'Дзюдо, самбо, большой теннис', 1, '', 'ул. Коминтерна, 16', '', ''),
(26, 56.838519, 60.656638, 'Студенческий центр \"Звездный\"', 'Фитнес', 1, '', 'ул. Фонвизина, 1/3', '', ''),
(27, 56.838469, 60.658281, 'ФОК - Физкультурно-оздоровительный комплекс', 'Бадминтон, волейбол, оздоровительная физкультура', 1, '', 'ул. Фонвизина, 5', '', ''),
(28, 56.838925, 60.657902, 'Мини-футбольное поле', 'Футбол', 1, '', 'ул. Фонвизина, 5\nВход напротив ФОК', '', ''),
(29, 56.837608, 60.65741, 'Спортзал \"Тайпинг\"', 'Тайский бокс', 1, '', 'ул. Фонвизина, 4', '', ''),
(30, 56.817493, 60.610275, 'Общежитие №1', '', 2, '', 'ул. Большакова, 79', '+ 7 (343) 257-33-95', ''),
(31, 56.817612, 60.610958, 'Общежитие №2', '', 2, '', 'ул. Большакова, 77', '+ 7 (343) 257-92-79', ''),
(32, 56.840276, 60.657265, 'Общежитие №3', ' ', 2, ' ', 'ул. Малышева, 140', '+ 7 (343) 375-47-10', ' '),
(33, 56.817872, 60.612757, 'Общежитие №4', ' ', 2, ' ', 'ул. Большакова, 71', '+ 7 (343) 257-25-23', ' '),
(34, 56.840503, 60.65893, 'Общежитие №5', ' ', 2, ' ', 'ул. Малышева, 144', '+ 7 (343) 375-45-46', ' '),
(35, 56.81731, 60.611993, 'Общежитие №6', ' ', 2, ' ', 'ул. Чапаева, 16А', '+ 7 (343) 257-01-61', ' '),
(36, 56.838533, 60.656269, 'Общежитие №7', ' ', 2, ' ', 'ул. Коминтерна, 3', '+ 7 (343)375-45-42', ' '),
(37, 56.837755, 60.659961, 'Общежитие №8', ' ', 2, ' ', 'ул. Комсомольская, 70', '+ 7 (343) 375-48-67', ' '),
(38, 56.842216, 60.641321, 'Общежитие №10', ' ', 2, ' ', 'пр. Ленина, 66', '+ 7 (343) 375-45-61', ' '),
(39, 56.837273, 60.656808, 'Общежитие №11', ' ', 2, ' ', 'ул. Коминтерна, 5', '+ 7 (343) 375-44-92', ' '),
(40, 56.837608, 60.65741, 'Общежитие №12', ' ', 2, ' ', 'ул. Фонвизина, 4', '+ 7 (343) 375-46-60', ' '),
(41, 56.839528, 60.659054, 'Общежитие №13', ' ', 2, ' ', 'ул. Комсомольская, 66А', '+ 7 (343) 375-47-97', ' '),
(42, 56.839316, 60.655892, 'Общежитие №14', ' ', 2, ' ', 'ул. Коминтерна, 1А', '+ 7 (343) 375-45-36', ' '),
(43, 56.836746, 60.65794, 'Общежитие №15', ' ', 2, ' ', 'ул. Коминтерна, 11', '+ 7 (343) 375-47-19', ' '),
(44, 56.841568, 60.658004, 'Общежитие №16', ' ', 2, ' ', 'ул. Малышева, 127А', '+ 7 (343) 375-47-50', ' '),
(45, 56.840124, 60.660779, 'Медико-санитарная часть', 'Часы работы: пн-пт 8:00 -18:00', 3, ' ', 'ул. Комсомольская, 59', '+ 7 (343) 375-94-77', 'Расписание работы врачей:\nhttps://med.urfu.ru/ru/attendance/schedule/'),
(47, 56.843731, 60.651326, 'Памятник С.М. Кирову​​​​​​​', 'Памятник появился около УПИ в 70-х годах и сразу стал местом встреч студентов «У сапога» (почему-то в обмундировании товарища Кирова самым примечательным студентам показались сапоги). Во время городских праздников здесь часто проводятся концерты.', 15, '', 'Напротив Главного учебного корпуса (ул. Мира, 19)\r\n主教学楼对面（Mira St.，19）', '', ''),
(48, 56.842515, 60.648986, 'Памятник участникам Великой Отечественной войны', 'Памятник студентам, преподавателям и сотрудникам УПИ, участвовавшим в Великой Отечественной войне. Надпись на постаменте гласит: \"Память о героях Великой Отечественной войны 1941 - 1945 гг. не сотрут века. От комсомольцев Уральского Политехнического Института им. С. М. Кирова\".', 15, '', 'Находится у входа в корпус ХТИ и ИНМТ (ул. Мира, 28)\r\n位于HTI和INMT大楼的入口处（Mira str。，28）', '', ''),
(49, 56.843427, 60.648619, 'Памятник футболистам', 'Трёхметровые фигуры футболистов вырезали из нержавеющей стали и собирали буквально по кусочкам. Они посвящены самому масштабному спортивному событию города - Чемпионату мира по футболу 2018 года', 15, '', 'Находится на площади Кирова\r\n位于基洛夫广场', '', ''),
(50, 56.839762, 60.616379, 'Памятник Я.М. Свердлову', 'Свердлов Яков Михайлович – один из видных политических деятелей Советской власти. Он был открыт в 1927 году. Автор проекта – скульптор Сигизмунд Добровский, архитектор Матвей Харламов. Яков Свердлов стоит в полный рост, а его рука динамично отведена в сторону. Эта поза была характерна для большевиков во время публичных выступлений. Высота скульптуры – 3,93 метра, она стоит на постаменте 5,2 метров. Считается одной из самых узнаваемых композиций города.', 15, '', 'Находится напротив корпуса УрГУ (проспект Ленина, 51)\r\n位于USU大楼对面（Lenin Avenue，51）', '', ''),
(51, 56.839627, 60.607787, 'Памятник А.С. Попову', 'Монумент посвящен Александру Степановичу Попову – основателю радио. Был открыт в 1975 году. Находится в центре города, рядом с Духовным училищем, в котором учился Александр Степанович. Автор памятника – скульптор В. Е. Егоров и архитектор П. Д. Деминцев. В 2005 году монумент отреставрировали, покрасили под бронзу. В том же году он был освящен Екатеринбургским и Верхотурским архиепископом.', 15, '', 'Находится в сквере Попова у Администрации Губернатора Свердловской области (ул. Горького, 21)\r\n位于斯维尔德洛夫斯克州州长办公室附近的波波夫广场（Gorky str。，21）', '', ''),
(60, 56.79523337617978, 59.923104954373315, 'Дом мой!', 'description', 0, 'Буква аудитории', 'Адрес', 'Контакты', 'Сайт'),
(61, 56.86141338223978, 60.708310420226056, 'Какое-то озеро', 'description', 0, 'Буква аудитории', 'Адрес', 'Контакты', 'Сайт');

-- --------------------------------------------------------

--
-- Структура таблицы `point_photoes`
--

CREATE TABLE `point_photoes` (
  `id` int(11) NOT NULL,
  `point_id` int(11) NOT NULL,
  `photo` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `point_photoes`
--

INSERT INTO `point_photoes` (`id`, `point_id`, `photo`) VALUES
(1, 0, 'GUK/GUK_1.png'),
(2, 0, 'GUK/GUK_2.png'),
(3, 0, 'GUK/GUK_3.png'),
(4, 1, 'IRIT_RTF/RTF_1.png'),
(5, 1, 'IRIT_RTF/RTF_2.png'),
(6, 1, 'IRIT_RTF/RTF_3.png'),
(7, 2, 'IENIM/IENIM_1.jpg'),
(8, 2, 'IENIM/IENIM_2.jpg'),
(9, 2, 'IENIM/IENIM_3.jpg'),
(10, 3, 'UGI/UGI_1.png'),
(11, 3, 'UGI/UGI_2.png'),
(12, 3, 'UGI/UGI_3.png'),
(13, 5, 'INEU_I/INEU_I_1.png'),
(14, 5, 'INEU_I/INEU_I_2.png'),
(15, 5, 'INEU_I/INEU_I_3.png'),
(16, 6, 'ELECTR/ELECTR_1.png'),
(17, 6, 'ELECTR/ELECTR_2.png'),
(18, 6, 'ELECTR/ELECTR_3.png'),
(19, 7, 'MEXM/MEXM_1.png'),
(20, 7, 'MEXM/MEXM_2.png'),
(21, 8, 'ISA/ISA_1.png'),
(22, 8, 'ISA/ISA_2.png'),
(23, 8, 'ISA/ISA_3.png'),
(24, 10, 'FTI/FTI_1.png'),
(25, 10, 'FTI/FTI_2.png'),
(26, 10, 'FTI/FTI_3.png'),
(27, 11, 'URALENIN/URALENIN_2.png'),
(28, 11, 'URALENIN/URALENIN_3.png'),
(29, 11, 'URALENIN/URALENIN_1.png'),
(30, 12, 'VOENKA/VOENKA_1.png'),
(31, 12, 'VOENKA/VOENKA_2.png'),
(32, 12, 'VOENKA/VOENKA_3.png'),
(33, 13, 'HTI/HTI_1.png'),
(34, 13, 'HTI/HTI_2.png'),
(35, 13, 'HTI/HTI_3.png'),
(36, 14, 'INMT/INMT_1.png'),
(37, 14, 'INMT/INMT_2.png'),
(38, 14, 'INMT/INMT_3.png'),
(39, 47, 'Routes/ROUTE_0/Point_47_0.jpg'),
(40, 48, 'Routes/ROUTE_0/Point_48_0.jpg'),
(41, 49, 'Routes/ROUTE_0/Point_49_0.jpg'),
(42, 50, 'Routes/ROUTE_0/Point_50_0.jpg'),
(43, 51, 'Routes/ROUTE_0/Point_51_0.jpg'),
(44, 47, 'Routes/ROUTE_0/Point_47_1.jpg'),
(45, 47, 'Routes/ROUTE_0/Point_47_2.jpg'),
(46, 48, 'Routes/ROUTE_0/Point_48_1.jpg'),
(47, 48, 'Routes/ROUTE_0/Point_48_2.jpg'),
(48, 49, 'Routes/ROUTE_0/Point_49_1.jpg'),
(49, 49, 'Routes/ROUTE_0/Point_49_2.jpg'),
(50, 50, 'Routes/ROUTE_0/Point_50_1.jpg'),
(51, 50, 'Routes/ROUTE_0/Point_50_2.jpg'),
(52, 51, 'Routes/ROUTE_0/Point_51_1.jpg'),
(53, 51, 'Routes/ROUTE_0/Point_51_2.jpg'),
(54, 45, 'MSP/MSP_1.jpg'),
(55, 45, 'MSP/MSP_2.jpg'),
(56, 45, 'MSP/MSP_3.jpg'),
(57, 7, 'MEXM/MEXM_3.png'),
(58, 18, 'IENiM/IENiM_1.jpg'),
(59, 18, 'IENiM/IENiM_2.jpg'),
(60, 18, 'IENiM/IENiM_3.jpg');

-- --------------------------------------------------------

--
-- Структура таблицы `route`
--

CREATE TABLE `route` (
  `id` int(11) NOT NULL,
  `route_name` text NOT NULL,
  `route_category_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `route`
--

INSERT INTO `route` (`id`, `route_name`, `route_category_id`) VALUES
(0, 'Топ 5 памятников около УрФУ', 15);

-- --------------------------------------------------------

--
-- Структура таблицы `route_chinese`
--

CREATE TABLE `route_chinese` (
  `id` int(11) NOT NULL,
  `route_alt_name` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `route_chinese`
--

INSERT INTO `route_chinese` (`id`, `route_alt_name`) VALUES
(15, 'UrFU附近的5大古迹');

-- --------------------------------------------------------

--
-- Структура таблицы `route_english`
--

CREATE TABLE `route_english` (
  `id` int(11) NOT NULL,
  `route_alt_name` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `route_english`
--

INSERT INTO `route_english` (`id`, `route_alt_name`) VALUES
(15, 'Top 5 memorials near UrFU');

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `login` text NOT NULL,
  `password` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `users`
--

INSERT INTO `users` (`id`, `login`, `password`) VALUES
(1, 'admin', 'd41d8cd98f00b204e9800998ecf8427e');

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
-- Индексы таблицы `category_english`
--
ALTER TABLE `category_english`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `data_chinese`
--
ALTER TABLE `data_chinese`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `data_english`
--
ALTER TABLE `data_english`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `point`
--
ALTER TABLE `point`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `point_photoes`
--
ALTER TABLE `point_photoes`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `route`
--
ALTER TABLE `route`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `route_chinese`
--
ALTER TABLE `route_chinese`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `route_english`
--
ALTER TABLE `route_english`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `users`
--
ALTER TABLE `users`
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
-- AUTO_INCREMENT для таблицы `category_english`
--
ALTER TABLE `category_english`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT для таблицы `data_english`
--
ALTER TABLE `data_english`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT для таблицы `point`
--
ALTER TABLE `point`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT для таблицы `point_photoes`
--
ALTER TABLE `point_photoes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT для таблицы `route`
--
ALTER TABLE `route`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT для таблицы `route_chinese`
--
ALTER TABLE `route_chinese`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT для таблицы `route_english`
--
ALTER TABLE `route_english`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT для таблицы `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

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
