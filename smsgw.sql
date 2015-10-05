-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Máquina: localhost
-- Data de Criação: 05-Out-2015 às 17:33
-- Versão do servidor: 5.5.44-0ubuntu0.14.04.1
-- versão do PHP: 5.5.9-1ubuntu4.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de Dados: `smsgw`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `receiver`
--

CREATE TABLE IF NOT EXISTS `receiver` (
  `receiverAddress` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`receiverAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `Receivers`
--

CREATE TABLE IF NOT EXISTS `Receivers` (
  `requestid` int(11) NOT NULL,
  `receiverAddress` int(11) NOT NULL,
  PRIMARY KEY (`requestid`,`receiverAddress`),
  KEY `receiverAddress` (`receiverAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `request`
--

CREATE TABLE IF NOT EXISTS `request` (
  `requestid` int(11) NOT NULL AUTO_INCREMENT,
  `stateid` int(11) NOT NULL,
  `senderAddress` int(11) NOT NULL,
  `body` varchar(250) NOT NULL,
  PRIMARY KEY (`requestid`),
  KEY `stateid` (`stateid`),
  KEY `senderAddress` (`senderAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `sender`
--

CREATE TABLE IF NOT EXISTS `sender` (
  `senderAddress` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `notifications` tinyint(1) NOT NULL,
  PRIMARY KEY (`senderAddress`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `sender`
--

INSERT INTO `sender` (`senderAddress`, `name`, `notifications`) VALUES
(916056618, 'luis duarte', 1);

-- --------------------------------------------------------

--
-- Estrutura da tabela `state`
--

CREATE TABLE IF NOT EXISTS `state` (
  `stateid` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(100) NOT NULL,
  PRIMARY KEY (`stateid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `Receivers`
--
ALTER TABLE `Receivers`
  ADD CONSTRAINT `Receivers_ibfk_1` FOREIGN KEY (`requestid`) REFERENCES `request` (`requestid`),
  ADD CONSTRAINT `Receivers_ibfk_2` FOREIGN KEY (`receiverAddress`) REFERENCES `receiver` (`receiverAddress`);

--
-- Limitadores para a tabela `request`
--
ALTER TABLE `request`
  ADD CONSTRAINT `request_ibfk_1` FOREIGN KEY (`stateid`) REFERENCES `state` (`stateid`),
  ADD CONSTRAINT `request_ibfk_2` FOREIGN KEY (`senderAddress`) REFERENCES `sender` (`senderAddress`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
