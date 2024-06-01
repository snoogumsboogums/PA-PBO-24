-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 01, 2024 at 12:22 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbjava`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbakun`
--

CREATE TABLE `tbakun` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbakun`
--

INSERT INTO `tbakun` (`id`, `username`, `password`) VALUES
(23, 'yuka', '123'),
(24, 'puput', '123'),
(25, 'tomo', '123');

-- --------------------------------------------------------

--
-- Table structure for table `tbdokter`
--

CREATE TABLE `tbdokter` (
  `kodeDokter` varchar(5) NOT NULL,
  `namaDokter` text NOT NULL,
  `gaji` float NOT NULL,
  `spesialis` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbdokter`
--

INSERT INTO `tbdokter` (`kodeDokter`, `namaDokter`, `gaji`, `spesialis`) VALUES
('J01', 'Dio Dharmaesa Sp.Kj ', 100000, 'jiwa'),
('M01', 'Muhammda Dirga Sp.M ', 900000, 'mata');

-- --------------------------------------------------------

--
-- Table structure for table `tbjadwaldokter`
--

CREATE TABLE `tbjadwaldokter` (
  `idJadwal` int(11) NOT NULL,
  `kodeDokter` varchar(50) DEFAULT NULL,
  `namaDokter` text DEFAULT NULL,
  `spesialis` text NOT NULL,
  `tanggal` date DEFAULT NULL,
  `jamMulai` text DEFAULT NULL,
  `jamSelesai` text DEFAULT NULL,
  `kuota` int(3) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbjadwaldokter`
--

INSERT INTO `tbjadwaldokter` (`idJadwal`, `kodeDokter`, `namaDokter`, `spesialis`, `tanggal`, `jamMulai`, `jamSelesai`, `kuota`) VALUES
(23, 'm01', 'Muhammad Ditut Sp.M ', 'mata', '2024-08-09', '11.00', '12.30', 2);

-- --------------------------------------------------------

--
-- Table structure for table `tbjanjikonsul`
--

CREATE TABLE `tbjanjikonsul` (
  `idJanji` int(11) NOT NULL,
  `idJadwal` int(11) DEFAULT NULL,
  `idPasien` int(11) DEFAULT NULL,
  `namaDokter` text DEFAULT NULL,
  `namaPasien` text DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `jam` text DEFAULT NULL,
  `keluhan` text NOT NULL,
  `resep` text DEFAULT NULL,
  `harga` int(11) DEFAULT 0,
  `status` varchar(20) NOT NULL DEFAULT 'Menunggu pembayaran'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbjanjikonsul`
--

INSERT INTO `tbjanjikonsul` (`idJanji`, `idJadwal`, `idPasien`, `namaDokter`, `namaPasien`, `tanggal`, `jam`, `keluhan`, `resep`, `harga`, `status`) VALUES
(27, 23, 23, 'Muhammad Ditut Sp.M ', 'Yuka', '2024-08-09', '11.00 - 12.30', 'Sakit', 'Panadol', 20000, 'Menunggu pembayaran');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbakun`
--
ALTER TABLE `tbakun`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbdokter`
--
ALTER TABLE `tbdokter`
  ADD PRIMARY KEY (`kodeDokter`);

--
-- Indexes for table `tbjadwaldokter`
--
ALTER TABLE `tbjadwaldokter`
  ADD PRIMARY KEY (`idJadwal`),
  ADD KEY `sip` (`kodeDokter`);

--
-- Indexes for table `tbjanjikonsul`
--
ALTER TABLE `tbjanjikonsul`
  ADD PRIMARY KEY (`idJanji`),
  ADD KEY `idJadwal` (`idJadwal`),
  ADD KEY `idPasien` (`idPasien`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbakun`
--
ALTER TABLE `tbakun`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `tbjadwaldokter`
--
ALTER TABLE `tbjadwaldokter`
  MODIFY `idJadwal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `tbjanjikonsul`
--
ALTER TABLE `tbjanjikonsul`
  MODIFY `idJanji` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbjadwaldokter`
--
ALTER TABLE `tbjadwaldokter`
  ADD CONSTRAINT `tbjadwaldokter_ibfk_1` FOREIGN KEY (`kodeDokter`) REFERENCES `tbdokter` (`kodeDokter`);

--
-- Constraints for table `tbjanjikonsul`
--
ALTER TABLE `tbjanjikonsul`
  ADD CONSTRAINT `tbjanjikonsul_ibfk_1` FOREIGN KEY (`idJadwal`) REFERENCES `tbjadwaldokter` (`idJadwal`),
  ADD CONSTRAINT `tbjanjikonsul_ibfk_2` FOREIGN KEY (`idPasien`) REFERENCES `tbakun` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
