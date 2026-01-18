-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               9.5.0 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.14.0.7165
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for quanlytiemnet_simple
CREATE DATABASE IF NOT EXISTS `quanlytiemnet_simple` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `quanlytiemnet_simple`;

-- Dumping structure for table quanlytiemnet_simple.chitiethoadon
CREATE TABLE IF NOT EXISTS `chitiethoadon` (
  `MaCTHD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaHD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `LoaiChiTiet` enum('GIOCHOI','DICHVU') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MoTa` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoLuong` decimal(10,2) NOT NULL DEFAULT '1.00',
  `DonGia` decimal(10,2) NOT NULL,
  `ThanhTien` decimal(12,2) NOT NULL,
  PRIMARY KEY (`MaCTHD`),
  KEY `idx_mahd_ct` (`MaHD`),
  KEY `idx_loaict` (`LoaiChiTiet`),
  CONSTRAINT `fk_cthd_hoadon` FOREIGN KEY (`MaHD`) REFERENCES `hoadon` (`MaHD`),
  CONSTRAINT `chk_dongia_ct` CHECK ((`DonGia` >= 0)),
  CONSTRAINT `chk_soluong_ct` CHECK ((`SoLuong` > 0)),
  CONSTRAINT `chk_thanhtien_ct` CHECK ((`ThanhTien` = (`SoLuong` * `DonGia`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.chitiethoadon: ~12 rows (approximately)
INSERT INTO `chitiethoadon` (`MaCTHD`, `MaHD`, `LoaiChiTiet`, `MoTa`, `SoLuong`, `DonGia`, `ThanhTien`) VALUES
	('CTHD001', 'HD001', 'GIOCHOI', 'Tiền giờ chơi', 2.00, 5000.00, 10000.00),
	('CTHD002', 'HD001', 'DICHVU', 'Sting Dâu', 1.00, 12000.00, 12000.00),
	('CTHD003', 'HD002', 'GIOCHOI', 'Tiền giờ chơi', 3.00, 5000.00, 15000.00),
	('CTHD004', 'HD002', 'DICHVU', 'Mì Tôm Trứng', 1.00, 25000.00, 25000.00),
	('CTHD005', 'HD002', 'DICHVU', 'Sting Dâu', 1.00, 12000.00, 12000.00),
	('CTHD006', 'HD003', 'GIOCHOI', 'Tiền giờ chơi', 1.50, 12000.00, 18000.00),
	('CTHD007', 'HD003', 'DICHVU', 'Cafe Sữa Đá', 2.00, 15000.00, 30000.00),
	('CTHD008', 'HD004', 'GIOCHOI', 'Trừ giờ combo/gói', 2.00, 0.00, 0.00),
	('CTHD009', 'HD005', 'GIOCHOI', 'Tiền giờ chơi', 4.00, 8000.00, 32000.00),
	('CTHD010', 'HD005', 'DICHVU', 'Cơm Chiên Dương Châu', 1.00, 35000.00, 35000.00),
	('CTHD011', 'HD005', 'DICHVU', 'Coca Cola', 1.00, 12000.00, 12000.00),
	('CTHD012', 'HD006', 'GIOCHOI', 'Tiền giờ chơi', 1.00, 6000.00, 6000.00);

-- Dumping structure for table quanlytiemnet_simple.chitietphieunhap
CREATE TABLE IF NOT EXISTS `chitietphieunhap` (
  `MaCTPN` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaPhieuNhap` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaDV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoLuong` int NOT NULL,
  `GiaNhap` decimal(10,2) NOT NULL COMMENT 'Giá nhập từ NCC',
  `ThanhTien` decimal(12,2) NOT NULL COMMENT 'SoLuong * GiaNhap',
  PRIMARY KEY (`MaCTPN`),
  KEY `idx_maphieunhap_ct` (`MaPhieuNhap`),
  KEY `idx_madv_ct` (`MaDV`),
  CONSTRAINT `fk_ctpn_dichvu` FOREIGN KEY (`MaDV`) REFERENCES `dichvu` (`MaDV`),
  CONSTRAINT `fk_ctpn_phieunhap` FOREIGN KEY (`MaPhieuNhap`) REFERENCES `phieunhaphang` (`MaPhieuNhap`) ON DELETE CASCADE,
  CONSTRAINT `chk_gianhap` CHECK ((`GiaNhap` >= 0)),
  CONSTRAINT `chk_soluong_pn` CHECK ((`SoLuong` > 0)),
  CONSTRAINT `chk_thanhtien_pn` CHECK ((`ThanhTien` = (`SoLuong` * `GiaNhap`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.chitietphieunhap: ~6 rows (approximately)
INSERT INTO `chitietphieunhap` (`MaCTPN`, `MaPhieuNhap`, `MaDV`, `SoLuong`, `GiaNhap`, `ThanhTien`) VALUES
	('CTPN001', 'PN001', 'DV001', 100, 8000.00, 800000.00),
	('CTPN002', 'PN001', 'DV002', 100, 8000.00, 800000.00),
	('CTPN003', 'PN001', 'DV005', 100, 4000.00, 400000.00),
	('CTPN004', 'PN002', 'DV003', 200, 4000.00, 800000.00),
	('CTPN005', 'PN004', 'DV010', 20, 10000.00, 200000.00),
	('CTPN006', 'PN003', 'DV009', 100, 18000.00, 1800000.00);

-- Dumping structure for table quanlytiemnet_simple.chuongtrinhkhuyenmai
CREATE TABLE IF NOT EXISTS `chuongtrinhkhuyenmai` (
  `MaCTKM` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TenCT` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `LoaiKM` enum('PHANTRAM','SOTIEN','TANGGIO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PHANTRAM',
  `GiaTriKM` decimal(10,2) NOT NULL COMMENT '% hoặc số tiền hoặc số giờ',
  `DieuKienToiThieu` decimal(12,2) DEFAULT '0.00' COMMENT 'Số tiền nạp tối thiểu',
  `NgayBatDau` datetime NOT NULL,
  `NgayKetThuc` datetime NOT NULL,
  `TrangThai` enum('HOATDONG','NGUNG','HETHAN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'HOATDONG',
  PRIMARY KEY (`MaCTKM`),
  KEY `idx_trangthai_km` (`TrangThai`),
  KEY `idx_ngay_km` (`NgayBatDau`,`NgayKetThuc`),
  CONSTRAINT `chk_dieukien` CHECK ((`DieuKienToiThieu` >= 0)),
  CONSTRAINT `chk_giatrikm` CHECK ((`GiaTriKM` > 0)),
  CONSTRAINT `chk_ngaykhuyenmai` CHECK ((`NgayKetThuc` > `NgayBatDau`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.chuongtrinhkhuyenmai: ~4 rows (approximately)
INSERT INTO `chuongtrinhkhuyenmai` (`MaCTKM`, `TenCT`, `LoaiKM`, `GiaTriKM`, `DieuKienToiThieu`, `NgayBatDau`, `NgayKetThuc`, `TrangThai`) VALUES
	('KM001', 'Khai trương', 'PHANTRAM', 20.00, 50000.00, '2023-01-01 00:00:00', '2023-01-31 00:00:00', 'HETHAN'),
	('KM002', 'Chào Hè', 'TANGGIO', 2.00, 100000.00, '2023-06-01 00:00:00', '2023-08-31 00:00:00', 'HETHAN'),
	('KM003', 'Nạp Lần Đầu', 'PHANTRAM', 50.00, 20000.00, '2024-01-01 00:00:00', '2024-12-31 00:00:00', 'HOATDONG'),
	('KM004', 'Tặng Tiền', 'SOTIEN', 10000.00, 100000.00, '2024-01-01 00:00:00', '2024-06-01 00:00:00', 'HOATDONG');

-- Dumping structure for table quanlytiemnet_simple.dichvu
CREATE TABLE IF NOT EXISTS `dichvu` (
  `MaDV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TenDV` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `LoaiDV` enum('DOUONG','THUCPHAM','KHAC') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'DOUONG',
  `DonGia` decimal(10,2) NOT NULL,
  `DonViTinh` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Cái',
  `SoLuongTon` int DEFAULT '0',
  `TrangThai` enum('CONHANG','HETHANG') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CONHANG',
  PRIMARY KEY (`MaDV`),
  KEY `idx_loaidv` (`LoaiDV`),
  KEY `idx_trangthai_dv` (`TrangThai`),
  CONSTRAINT `chk_dongia` CHECK ((`DonGia` >= 0)),
  CONSTRAINT `chk_soluongton` CHECK ((`SoLuongTon` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.dichvu: ~10 rows (approximately)
INSERT INTO `dichvu` (`MaDV`, `TenDV`, `LoaiDV`, `DonGia`, `DonViTinh`, `SoLuongTon`, `TrangThai`) VALUES
	('DV001', 'Sting Dâu', 'DOUONG', 12000.00, 'Chai', 99, 'CONHANG'),
	('DV002', 'Coca Cola', 'DOUONG', 12000.00, 'Lon', 100, 'CONHANG'),
	('DV003', 'Mì Tôm Trứng', 'THUCPHAM', 25000.00, 'Tô', 98, 'CONHANG'),
	('DV004', 'Cơm Chiên Dương Châu', 'THUCPHAM', 35000.00, 'Dĩa', 29, 'CONHANG'),
	('DV005', 'Nước Suối', 'DOUONG', 8000.00, 'Chai', 120, 'CONHANG'),
	('DV006', 'Bò Húc', 'DOUONG', 15000.00, 'Lon', 80, 'CONHANG'),
	('DV007', 'Khoai Tây Chiên', 'THUCPHAM', 20000.00, 'Dĩa', 40, 'CONHANG'),
	('DV008', 'Cafe Sữa Đá', 'DOUONG', 15000.00, 'Ly', 200, 'CONHANG'),
	('DV009', 'Thẻ Game Garena 20k', 'KHAC', 20000.00, 'Thẻ', 50, 'CONHANG'),
	('DV010', 'Bánh Mì Ốp La', 'THUCPHAM', 20000.00, 'Cái', 20, 'CONHANG');

-- Dumping structure for table quanlytiemnet_simple.goidichvu
CREATE TABLE IF NOT EXISTS `goidichvu` (
  `MaGoi` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TenGoi` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `LoaiGoi` enum('THEOGIO','THEONGAY','THEOTUAN','THEOTHANG') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'THEOGIO',
  `SoGio` decimal(10,2) NOT NULL COMMENT 'Tổng số giờ trong gói',
  `SoNgayHieuLuc` int DEFAULT '30' COMMENT 'Số ngày có hiệu lực',
  `GiaGoc` decimal(12,2) NOT NULL COMMENT 'Giá nếu mua lẻ',
  `GiaGoi` decimal(12,2) NOT NULL COMMENT 'Giá bán gói',
  `ApDungChoKhu` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Tên khu áp dụng',
  `TrangThai` enum('HOATDONG','NGUNG') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'HOATDONG',
  PRIMARY KEY (`MaGoi`),
  KEY `idx_trangthai_goi` (`TrangThai`),
  KEY `idx_loaigoi` (`LoaiGoi`),
  CONSTRAINT `chk_giagoihople` CHECK (((`GiaGoi` > 0) and (`GiaGoi` <= `GiaGoc`))),
  CONSTRAINT `chk_sogio` CHECK ((`SoGio` > 0)),
  CONSTRAINT `chk_songayhieuluc` CHECK ((`SoNgayHieuLuc` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.goidichvu: ~5 rows (approximately)
INSERT INTO `goidichvu` (`MaGoi`, `TenGoi`, `LoaiGoi`, `SoGio`, `SoNgayHieuLuc`, `GiaGoc`, `GiaGoi`, `ApDungChoKhu`, `TrangThai`) VALUES
	('GOI001', 'Combo Sáng (3h)', 'THEOGIO', 3.00, 30, 15000.00, 12000.00, NULL, 'HOATDONG'),
	('GOI002', 'Combo Đêm (7h)', 'THEOGIO', 7.00, 30, 35000.00, 25000.00, NULL, 'HOATDONG'),
	('GOI003', 'Gói Ngày (10h)', 'THEONGAY', 10.00, 30, 50000.00, 40000.00, NULL, 'HOATDONG'),
	('GOI004', 'Gói Tuần (50h)', 'THEOTUAN', 50.00, 30, 250000.00, 200000.00, NULL, 'HOATDONG'),
	('GOI005', 'Test Gói Ngắn', 'THEOGIO', 1.00, 30, 5000.00, 4000.00, NULL, 'NGUNG');

-- Dumping structure for table quanlytiemnet_simple.goidichvu_khachhang
CREATE TABLE IF NOT EXISTS `goidichvu_khachhang` (
  `MaGoiKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaGoi` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaNV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoGioBanDau` decimal(10,2) NOT NULL,
  `SoGioConLai` decimal(10,2) NOT NULL,
  `NgayMua` datetime DEFAULT CURRENT_TIMESTAMP,
  `NgayHetHan` datetime NOT NULL,
  `GiaMua` decimal(12,2) NOT NULL,
  `TrangThai` enum('CONHAN','HETHAN','DAHETGIO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CONHAN',
  PRIMARY KEY (`MaGoiKH`),
  KEY `idx_makh_goi` (`MaKH`),
  KEY `idx_makh_trangthai_goi` (`MaKH`,`TrangThai`),
  KEY `idx_trangthai_goikh` (`TrangThai`),
  KEY `idx_ngayhethan` (`NgayHetHan`),
  KEY `fk_goikh_goidichvu` (`MaGoi`),
  KEY `fk_goikh_nhanvien` (`MaNV`),
  CONSTRAINT `fk_goikh_goidichvu` FOREIGN KEY (`MaGoi`) REFERENCES `goidichvu` (`MaGoi`),
  CONSTRAINT `fk_goikh_khachhang` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`),
  CONSTRAINT `fk_goikh_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`),
  CONSTRAINT `chk_giamua` CHECK ((`GiaMua` > 0)),
  CONSTRAINT `chk_ngayhethan` CHECK ((`NgayHetHan` > `NgayMua`)),
  CONSTRAINT `chk_sogioconlai` CHECK ((`SoGioConLai` >= 0)),
  CONSTRAINT `chk_sogioconlai_max` CHECK ((`SoGioConLai` <= `SoGioBanDau`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.goidichvu_khachhang: ~4 rows (approximately)
INSERT INTO `goidichvu_khachhang` (`MaGoiKH`, `MaKH`, `MaGoi`, `MaNV`, `SoGioBanDau`, `SoGioConLai`, `NgayMua`, `NgayHetHan`, `GiaMua`, `TrangThai`) VALUES
	('GOIKH001', 'KH001', 'GOI001', 'NV002', 3.00, 0.00, '2026-01-12 08:23:21', '2026-02-11 08:23:21', 12000.00, 'DAHETGIO'),
	('GOIKH002', 'KH002', 'GOI002', 'NV002', 7.00, 5.50, '2026-01-13 08:23:21', '2026-02-12 08:23:21', 25000.00, 'CONHAN'),
	('GOIKH003', 'KH004', 'GOI004', 'NV002', 50.00, 48.00, '2026-01-14 08:23:21', '2026-02-13 08:23:21', 200000.00, 'CONHAN'),
	('GOIKH004', 'KH007', 'GOI003', 'NV004', 10.00, 10.00, '2026-01-14 08:23:21', '2026-02-13 08:23:21', 40000.00, 'CONHAN');

-- Dumping structure for table quanlytiemnet_simple.hoadon
CREATE TABLE IF NOT EXISTS `hoadon` (
  `MaHD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaPhien` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaNV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `NgayLap` datetime DEFAULT CURRENT_TIMESTAMP,
  `TienGioChoi` decimal(12,2) DEFAULT '0.00',
  `TienDichVu` decimal(12,2) DEFAULT '0.00',
  `TongTien` decimal(12,2) NOT NULL COMMENT 'TienGioChoi + TienDichVu',
  `GiamGia` decimal(12,2) DEFAULT '0.00',
  `ThanhToan` decimal(12,2) NOT NULL COMMENT 'TongTien - GiamGia',
  `PhuongThucTT` enum('TIENMAT','CHUYENKHOAN','MOMO','VNPAY','TAIKHOAN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TAIKHOAN',
  `TrangThai` enum('CHUATHANHTOAN','DATHANHTOAN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CHUATHANHTOAN',
  PRIMARY KEY (`MaHD`),
  UNIQUE KEY `uk_maphien` (`MaPhien`),
  KEY `idx_makh_hd` (`MaKH`),
  KEY `idx_ngaylap` (`NgayLap`),
  KEY `idx_trangthai_hd` (`TrangThai`),
  KEY `fk_hoadon_nhanvien` (`MaNV`),
  CONSTRAINT `fk_hoadon_khachhang` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`),
  CONSTRAINT `fk_hoadon_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`),
  CONSTRAINT `fk_hoadon_phien` FOREIGN KEY (`MaPhien`) REFERENCES `phiensudung` (`MaPhien`),
  CONSTRAINT `chk_giamgia` CHECK ((`GiamGia` >= 0)),
  CONSTRAINT `chk_giamgia_max` CHECK ((`GiamGia` <= `TongTien`)),
  CONSTRAINT `chk_thanhtoan_hd` CHECK ((`ThanhToan` = (`TongTien` - `GiamGia`))),
  CONSTRAINT `chk_tongtien_hd` CHECK ((`TongTien` = (`TienGioChoi` + `TienDichVu`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.hoadon: ~6 rows (approximately)
INSERT INTO `hoadon` (`MaHD`, `MaPhien`, `MaKH`, `MaNV`, `NgayLap`, `TienGioChoi`, `TienDichVu`, `TongTien`, `GiamGia`, `ThanhToan`, `PhuongThucTT`, `TrangThai`) VALUES
	('HD001', 'PS001', 'KH001', 'NV002', '2026-01-14 08:23:21', 10000.00, 12000.00, 22000.00, 0.00, 22000.00, 'TAIKHOAN', 'DATHANHTOAN'),
	('HD002', 'PS002', 'KH002', 'NV002', '2026-01-14 08:23:21', 15000.00, 37000.00, 52000.00, 0.00, 52000.00, 'TAIKHOAN', 'DATHANHTOAN'),
	('HD003', 'PS003', 'KH003', 'NV004', '2026-01-14 08:23:21', 18000.00, 30000.00, 48000.00, 0.00, 48000.00, 'TIENMAT', 'DATHANHTOAN'),
	('HD004', 'PS004', 'KH004', 'NV002', '2026-01-14 08:23:21', 0.00, 0.00, 0.00, 0.00, 0.00, 'TAIKHOAN', 'DATHANHTOAN'),
	('HD005', 'PS005', 'KH005', 'NV002', '2026-01-14 08:23:21', 32000.00, 47000.00, 79000.00, 0.00, 79000.00, 'TAIKHOAN', 'DATHANHTOAN'),
	('HD006', 'PS006', 'KH006', 'NV004', '2026-01-14 08:23:21', 6000.00, 0.00, 6000.00, 0.00, 6000.00, 'TAIKHOAN', 'DATHANHTOAN');

-- Dumping structure for table quanlytiemnet_simple.khachhang
CREATE TABLE IF NOT EXISTS `khachhang` (
  `MaKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Ho` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Ten` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoDienThoai` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TenDangNhap` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MatKhau` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoDu` decimal(12,2) DEFAULT '0.00',
  PRIMARY KEY (`MaKH`),
  UNIQUE KEY `uk_tendangnhap_kh` (`TenDangNhap`),
  KEY `idx_sodienthoai` (`SoDienThoai`),
  CONSTRAINT `chk_sodu` CHECK ((`SoDu` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.khachhang: ~15 rows (approximately)
INSERT INTO `khachhang` (`MaKH`, `Ho`, `Ten`, `SoDienThoai`, `TenDangNhap`, `MatKhau`, `SoDu`) VALUES
	('KH001', 'Nguyễn', 'Hoàng Nam', '0901234561', 'hoangnam', '123456', 73000.00),
	('KH002', 'Lê', 'Thị Hồng', '0901234562', 'hongle', '123456', 98000.00),
	('KH003', 'Trần', 'Minh Tuấn', '0901234563', 'tuantran', '123456', 48000.00),
	('KH004', 'Phạm', 'Quốc Anh', '0901234564', 'anhpham', '123456', 10000.00),
	('KH005', 'Hoàng', 'Thị Mai', '0901234565', 'maihoang', '123456', 0.00),
	('KH006', 'Phan', 'Tuấn Kiệt', '0901234567', 'tuankiet', '123456', 50000.00),
	('KH007', 'Vũ', 'Minh Hiếu', '0901234568', 'hieugaming', '123456', 630000.00),
	('KH008', 'Đặng', 'Thị Mai', '0901234569', 'mai_cherry', '123456', 10000.00),
	('KH009', 'Ngô', 'Bảo Long', '0901234570', 'long_dragon', '123456', 50000.00),
	('KH010', 'Bùi', 'Phương Thảo', '0901234571', 'thaocute', '123456', 45000.00),
	('KH011', 'Đỗ', 'Hùng Dũng', '0901234572', 'dung_lol', '123456', 15000.00),
	('KH012', 'Lý', 'Quang Hải', '0901234573', 'hai_fifa', '123456', 300000.00),
	('KH013', 'Trương', 'Mỹ Lan', '0901234574', 'lan_pubg', '123456', 8000.00),
	('KH014', 'Hồ', 'Tấn Tài', '0901234575', 'tai_valorant', '123456', 50000.00),
	('KH015', 'Dương', 'Văn Lâm', '0901234576', 'lam_csgo', '123456', 0.00);

-- Dumping structure for table quanlytiemnet_simple.khumay
CREATE TABLE IF NOT EXISTS `khumay` (
  `MaKhu` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TenKhu` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `GiaCoSo` decimal(10,2) NOT NULL COMMENT 'Giá cơ sở mỗi giờ',
  `SoMayToiDa` int DEFAULT '0',
  `TrangThai` enum('HOATDONG','NGUNG') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'HOATDONG',
  PRIMARY KEY (`MaKhu`),
  UNIQUE KEY `uk_tenkhu` (`TenKhu`),
  KEY `idx_trangthai_khu` (`TrangThai`),
  CONSTRAINT `chk_giacoso` CHECK ((`GiaCoSo` > 0)),
  CONSTRAINT `chk_somaytoida` CHECK ((`SoMayToiDa` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.khumay: ~5 rows (approximately)
INSERT INTO `khumay` (`MaKhu`, `TenKhu`, `GiaCoSo`, `SoMayToiDa`, `TrangThai`) VALUES
	('KHU001', 'Khu Thường (A)', 5000.00, 20, 'HOATDONG'),
	('KHU002', 'Khu VIP (B)', 8000.00, 15, 'HOATDONG'),
	('KHU003', 'Khu Thi Đấu (S)', 10000.00, 10, 'HOATDONG'),
	('KHU004', 'Khu Couple (C)', 12000.00, 5, 'HOATDONG'),
	('KHU005', 'Khu Hút Thuốc (D)', 6000.00, 15, 'HOATDONG');

-- Dumping structure for table quanlytiemnet_simple.lichsunaptien
CREATE TABLE IF NOT EXISTS `lichsunaptien` (
  `MaNap` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaNV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaCTKM` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SoTienNap` decimal(12,2) NOT NULL,
  `KhuyenMai` decimal(12,2) DEFAULT '0.00',
  `TongTienCong` decimal(12,2) NOT NULL COMMENT 'SoTienNap + KhuyenMai',
  `SoDuTruoc` decimal(12,2) NOT NULL,
  `SoDuSau` decimal(12,2) NOT NULL,
  `PhuongThuc` enum('TIENMAT','CHUYENKHOAN','MOMO','VNPAY','ZALOPAY','THE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TIENMAT',
  `MaGiaoDich` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NgayNap` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MaNap`),
  KEY `idx_makh_nap` (`MaKH`),
  KEY `idx_ngaynap` (`NgayNap`),
  KEY `idx_phuongthuc` (`PhuongThuc`),
  KEY `idx_mactkm` (`MaCTKM`),
  KEY `fk_naptien_nhanvien` (`MaNV`),
  CONSTRAINT `fk_naptien_khachhang` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`),
  CONSTRAINT `fk_naptien_khuyenmai` FOREIGN KEY (`MaCTKM`) REFERENCES `chuongtrinhkhuyenmai` (`MaCTKM`) ON DELETE SET NULL,
  CONSTRAINT `fk_naptien_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`),
  CONSTRAINT `chk_khuyenmai` CHECK ((`KhuyenMai` >= 0)),
  CONSTRAINT `chk_sodusau_nap` CHECK ((`SoDuSau` = (`SoDuTruoc` + `TongTienCong`))),
  CONSTRAINT `chk_sotiennap` CHECK ((`SoTienNap` > 0)),
  CONSTRAINT `chk_tongtien` CHECK ((`TongTienCong` = (`SoTienNap` + `KhuyenMai`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.lichsunaptien: ~10 rows (approximately)
INSERT INTO `lichsunaptien` (`MaNap`, `MaKH`, `MaNV`, `MaCTKM`, `SoTienNap`, `KhuyenMai`, `TongTienCong`, `SoDuTruoc`, `SoDuSau`, `PhuongThuc`, `MaGiaoDich`, `NgayNap`) VALUES
	('NAP001', 'KH001', 'NV002', 'KM003', 50000.00, 25000.00, 75000.00, 0.00, 75000.00, 'TIENMAT', NULL, '2026-01-14 08:23:21'),
	('NAP002', 'KH002', 'NV002', NULL, 100000.00, 0.00, 100000.00, 50000.00, 150000.00, 'CHUYENKHOAN', NULL, '2026-01-14 08:23:21'),
	('NAP003', 'KH004', 'NV002', 'KM004', 200000.00, 10000.00, 210000.00, 10000.00, 220000.00, 'MOMO', NULL, '2026-01-14 08:23:21'),
	('NAP004', 'KH005', 'NV002', NULL, 20000.00, 0.00, 20000.00, 0.00, 20000.00, 'TIENMAT', NULL, '2026-01-14 08:23:21'),
	('NAP005', 'KH007', 'NV004', 'KM003', 500000.00, 250000.00, 750000.00, 0.00, 750000.00, 'VNPAY', NULL, '2026-01-14 08:23:21'),
	('NAP006', 'KH001', 'NV002', NULL, 20000.00, 0.00, 20000.00, 75000.00, 95000.00, 'TIENMAT', NULL, '2026-01-14 08:23:21'),
	('NAP007', 'KH002', 'NV004', NULL, 50000.00, 0.00, 50000.00, 150000.00, 200000.00, 'CHUYENKHOAN', NULL, '2026-01-14 08:23:21'),
	('NAP008', 'KH006', 'NV004', NULL, 10000.00, 0.00, 10000.00, 5000.00, 15000.00, 'TIENMAT', NULL, '2026-01-14 08:23:21'),
	('NAP009', 'KH008', 'NV002', NULL, 10000.00, 0.00, 10000.00, 0.00, 10000.00, 'TIENMAT', NULL, '2026-01-14 08:23:21'),
	('NAP010', 'KH009', 'NV002', NULL, 50000.00, 0.00, 50000.00, 0.00, 50000.00, 'ZALOPAY', NULL, '2026-01-14 08:23:21');

-- Dumping structure for table quanlytiemnet_simple.maytinh
CREATE TABLE IF NOT EXISTS `maytinh` (
  `MaMay` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TenMay` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaKhu` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CauHinh` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `GiaMoiGio` decimal(10,2) NOT NULL,
  `TrangThai` enum('TRONG','DANGDUNG','BAOTRI') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TRONG',
  PRIMARY KEY (`MaMay`),
  UNIQUE KEY `uk_tenmay` (`TenMay`),
  KEY `idx_trangthai_may` (`TrangThai`),
  KEY `idx_makhu` (`MaKhu`),
  CONSTRAINT `fk_maytinh_khumay` FOREIGN KEY (`MaKhu`) REFERENCES `khumay` (`MaKhu`) ON DELETE SET NULL,
  CONSTRAINT `chk_giamoigio` CHECK ((`GiaMoiGio` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.maytinh: ~15 rows (approximately)
INSERT INTO `maytinh` (`MaMay`, `TenMay`, `MaKhu`, `CauHinh`, `GiaMoiGio`, `TrangThai`) VALUES
	('MAY001', 'MAY-A01', 'KHU001', 'i3 10100F, GTX 1050Ti, 8GB RAM', 5000.00, 'TRONG'),
	('MAY002', 'MAY-A02', 'KHU001', 'i3 10100F, GTX 1050Ti, 8GB RAM', 5000.00, 'TRONG'),
	('MAY003', 'MAY-A03', 'KHU001', 'i3 10100F, GTX 1050Ti, 8GB RAM', 5000.00, 'DANGDUNG'),
	('MAY004', 'MAY-A04', 'KHU001', 'i3 10100F, GTX 1050Ti, 8GB RAM', 5000.00, 'BAOTRI'),
	('MAY005', 'MAY-A05', 'KHU001', 'i3 10100F, GTX 1050Ti, 8GB RAM', 5000.00, 'TRONG'),
	('MAY006', 'MAY-B01', 'KHU002', 'i5 12400F, RTX 3060, 16GB RAM', 8000.00, 'TRONG'),
	('MAY007', 'MAY-B02', 'KHU002', 'i5 12400F, RTX 3060, 16GB RAM', 8000.00, 'DANGDUNG'),
	('MAY008', 'MAY-B03', 'KHU002', 'i5 12400F, RTX 3060, 16GB RAM', 8000.00, 'TRONG'),
	('MAY009', 'MAY-B04', 'KHU002', 'i5 12400F, RTX 3060, 16GB RAM', 8000.00, 'TRONG'),
	('MAY010', 'MAY-S01', 'KHU003', 'i7 13700K, RTX 4070, 32GB RAM', 10000.00, 'DANGDUNG'),
	('MAY011', 'MAY-S02', 'KHU003', 'i7 13700K, RTX 4070, 32GB RAM', 10000.00, 'TRONG'),
	('MAY012', 'MAY-C01', 'KHU004', 'i5 12400F, GTX 1660S, 16GB RAM (Ghế đôi)', 12000.00, 'TRONG'),
	('MAY013', 'MAY-C02', 'KHU004', 'i5 12400F, GTX 1660S, 16GB RAM (Ghế đôi)', 12000.00, 'DANGDUNG'),
	('MAY014', 'MAY-D01', 'KHU005', 'i3 12100F, GTX 1650, 8GB RAM', 6000.00, 'TRONG'),
	('MAY015', 'MAY-D02', 'KHU005', 'i3 12100F, GTX 1650, 8GB RAM', 6000.00, 'TRONG');

-- Dumping structure for table quanlytiemnet_simple.nhacungcap
CREATE TABLE IF NOT EXISTS `nhacungcap` (
  `MaNCC` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TenNCC` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoDienThoai` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DiaChi` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NguoiLienHe` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Tên người đại diện/liên hệ',
  `TrangThai` enum('HOATDONG','NGUNG') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'HOATDONG',
  PRIMARY KEY (`MaNCC`),
  KEY `idx_trangthai_ncc` (`TrangThai`),
  KEY `idx_tenncc` (`TenNCC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.nhacungcap: ~5 rows (approximately)
INSERT INTO `nhacungcap` (`MaNCC`, `TenNCC`, `SoDienThoai`, `Email`, `DiaChi`, `NguoiLienHe`, `TrangThai`) VALUES
	('NCC001', 'Công ty Pepsico', '02839123456', 'contact@pepsi.vn', 'Q1, TP.HCM', 'Anh Nam', 'HOATDONG'),
	('NCC002', 'Đại lý Mì Hảo Hảo', '02839123457', 'sales@acecook.vn', 'Tân Bình, TP.HCM', 'Chị Lan', 'HOATDONG'),
	('NCC003', 'Vi tính Lê Phụng', '02839123458', 'support@lephung.vn', 'Q3, TP.HCM', 'Anh Minh', 'HOATDONG'),
	('NCC004', 'Coca Cola VN', '02839123459', 'sales@coca.vn', 'Thủ Đức, TP.HCM', 'Anh Tú', 'HOATDONG'),
	('NCC005', 'Bánh Mì Staff', '02839123460', 'order@banhmi.vn', 'Q10, TP.HCM', 'Cô Ba', 'HOATDONG');

-- Dumping structure for table quanlytiemnet_simple.nhanvien
CREATE TABLE IF NOT EXISTS `nhanvien` (
  `MaNV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Ho` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Ten` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ChucVu` enum('QUANLY','NHANVIEN','THUNGAN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NHANVIEN',
  `TenDangNhap` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MatKhau` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TrangThai` enum('DANGLAMVIEC','NGHIVIEC') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'DANGLAMVIEC',
  PRIMARY KEY (`MaNV`),
  UNIQUE KEY `uk_tendangnhap_nv` (`TenDangNhap`),
  KEY `idx_trangthai_nv` (`TrangThai`),
  KEY `idx_chucvu` (`ChucVu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.nhanvien: ~5 rows (approximately)
INSERT INTO `nhanvien` (`MaNV`, `Ho`, `Ten`, `ChucVu`, `TenDangNhap`, `MatKhau`, `TrangThai`) VALUES
	('NV001', 'Nguyễn', 'Văn A', 'QUANLY', 'admin', 'password_hash_1', 'DANGLAMVIEC'),
	('NV002', 'Trần', 'Thị B', 'THUNGAN', 'thungan01', 'password_hash_2', 'DANGLAMVIEC'),
	('NV003', 'Lê', 'Văn C', 'NHANVIEN', 'kythuat01', 'password_hash_3', 'DANGLAMVIEC'),
	('NV004', 'Phạm', 'Thị D', 'THUNGAN', 'thungan02', 'password_hash_4', 'DANGLAMVIEC'),
	('NV005', 'Hoàng', 'Văn E', 'NHANVIEN', 'phucvu01', 'password_hash_5', 'NGHIVIEC');

-- Dumping structure for table quanlytiemnet_simple.phiensudung
CREATE TABLE IF NOT EXISTS `phiensudung` (
  `MaPhien` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaMay` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaNV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MaGoiKH` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `GioBatDau` datetime NOT NULL,
  `GioKetThuc` datetime DEFAULT NULL,
  `TongGio` decimal(10,2) DEFAULT '0.00' COMMENT 'Tổng giờ chơi',
  `GioSuDungTuGoi` decimal(10,2) DEFAULT '0.00' COMMENT 'Giờ dùng từ gói',
  `GioSuDungTuTaiKhoan` decimal(10,2) DEFAULT '0.00' COMMENT 'Giờ dùng từ tài khoản',
  `GiaMoiGio` decimal(10,2) NOT NULL COMMENT 'Giá tại thời điểm sử dụng',
  `TienGioChoi` decimal(12,2) DEFAULT '0.00' COMMENT 'Tiền giờ chơi',
  `LoaiThanhToan` enum('TAIKHOAN','GOI','KETHOP') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TAIKHOAN',
  `TrangThai` enum('DANGCHOI','DAKETTHUC') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'DANGCHOI',
  PRIMARY KEY (`MaPhien`),
  KEY `idx_makh_phien` (`MaKH`),
  KEY `idx_mamay_phien` (`MaMay`),
  KEY `idx_trangthai_phien` (`TrangThai`),
  KEY `idx_giobatdau` (`GioBatDau`),
  KEY `fk_phien_nhanvien` (`MaNV`),
  KEY `fk_phien_goikh` (`MaGoiKH`),
  CONSTRAINT `fk_phien_goikh` FOREIGN KEY (`MaGoiKH`) REFERENCES `goidichvu_khachhang` (`MaGoiKH`) ON DELETE SET NULL,
  CONSTRAINT `fk_phien_khachhang` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`),
  CONSTRAINT `fk_phien_maytinh` FOREIGN KEY (`MaMay`) REFERENCES `maytinh` (`MaMay`),
  CONSTRAINT `fk_phien_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`) ON DELETE SET NULL,
  CONSTRAINT `chk_giosdgoi` CHECK ((`GioSuDungTuGoi` >= 0)),
  CONSTRAINT `chk_giosdtk` CHECK ((`GioSuDungTuTaiKhoan` >= 0)),
  CONSTRAINT `chk_tiengiochoi` CHECK ((`TienGioChoi` >= 0)),
  CONSTRAINT `chk_tonggio` CHECK ((`TongGio` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.phiensudung: ~10 rows (approximately)
INSERT INTO `phiensudung` (`MaPhien`, `MaKH`, `MaMay`, `MaNV`, `MaGoiKH`, `GioBatDau`, `GioKetThuc`, `TongGio`, `GioSuDungTuGoi`, `GioSuDungTuTaiKhoan`, `GiaMoiGio`, `TienGioChoi`, `LoaiThanhToan`, `TrangThai`) VALUES
	('PS001', 'KH001', 'MAY001', 'NV002', NULL, '2024-02-01 08:00:00', '2024-02-01 10:00:00', 2.00, 0.00, 2.00, 5000.00, 10000.00, 'TAIKHOAN', 'DAKETTHUC'),
	('PS002', 'KH002', 'MAY002', 'NV002', NULL, '2024-02-01 09:00:00', '2024-02-01 12:00:00', 3.00, 0.00, 3.00, 5000.00, 15000.00, 'TAIKHOAN', 'DAKETTHUC'),
	('PS003', 'KH003', 'MAY012', 'NV004', NULL, '2024-02-01 14:00:00', '2024-02-01 15:30:00', 1.50, 0.00, 1.50, 12000.00, 18000.00, 'TAIKHOAN', 'DAKETTHUC'),
	('PS004', 'KH004', 'MAY010', 'NV002', 'GOIKH003', '2024-02-02 10:00:00', '2024-02-02 12:00:00', 2.00, 2.00, 0.00, 10000.00, 0.00, 'TAIKHOAN', 'DAKETTHUC'),
	('PS005', 'KH005', 'MAY006', 'NV002', NULL, '2024-02-02 18:00:00', '2024-02-02 22:00:00', 4.00, 0.00, 4.00, 8000.00, 32000.00, 'TAIKHOAN', 'DAKETTHUC'),
	('PS006', 'KH006', 'MAY014', 'NV004', NULL, '2024-02-03 08:00:00', '2024-02-03 09:00:00', 1.00, 0.00, 1.00, 6000.00, 6000.00, 'TAIKHOAN', 'DAKETTHUC'),
	('PS007', 'KH007', 'MAY011', 'NV002', 'GOIKH004', '2026-01-14 08:23:21', NULL, 0.00, 0.00, 0.00, 10000.00, 0.00, 'TAIKHOAN', 'DANGCHOI'),
	('PS008', 'KH008', 'MAY003', 'NV002', NULL, '2026-01-14 08:23:21', NULL, 0.00, 0.00, 0.00, 5000.00, 0.00, 'TAIKHOAN', 'DANGCHOI'),
	('PS009', 'KH002', 'MAY007', 'NV002', 'GOIKH002', '2026-01-14 08:23:21', NULL, 0.00, 0.00, 0.00, 8000.00, 0.00, 'TAIKHOAN', 'DANGCHOI'),
	('PS010', 'KH009', 'MAY013', 'NV004', NULL, '2026-01-14 08:23:21', NULL, 0.00, 0.00, 0.00, 12000.00, 0.00, 'TAIKHOAN', 'DANGCHOI');

-- Dumping structure for table quanlytiemnet_simple.phieunhaphang
CREATE TABLE IF NOT EXISTS `phieunhaphang` (
  `MaPhieuNhap` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaNCC` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaNV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `NgayNhap` datetime DEFAULT CURRENT_TIMESTAMP,
  `TongTien` decimal(15,2) NOT NULL DEFAULT '0.00',
  `TrangThai` enum('CHODUYET','DANHAP','DAHUY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CHODUYET',
  PRIMARY KEY (`MaPhieuNhap`),
  KEY `idx_mancc_pn` (`MaNCC`),
  KEY `idx_manv_pn` (`MaNV`),
  KEY `idx_ngaynhap` (`NgayNhap`),
  KEY `idx_trangthai_pn` (`TrangThai`),
  CONSTRAINT `fk_phieunhap_nhacungcap` FOREIGN KEY (`MaNCC`) REFERENCES `nhacungcap` (`MaNCC`),
  CONSTRAINT `fk_phieunhap_nhanvien` FOREIGN KEY (`MaNV`) REFERENCES `nhanvien` (`MaNV`),
  CONSTRAINT `chk_tongtien_pn` CHECK ((`TongTien` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.phieunhaphang: ~4 rows (approximately)
INSERT INTO `phieunhaphang` (`MaPhieuNhap`, `MaNCC`, `MaNV`, `NgayNhap`, `TongTien`, `TrangThai`) VALUES
	('PN001', 'NCC001', 'NV002', '2026-01-14 08:23:21', 2400000.00, 'DANHAP'),
	('PN002', 'NCC002', 'NV002', '2026-01-14 08:23:21', 1000000.00, 'DANHAP'),
	('PN003', 'NCC003', 'NV001', '2026-01-14 08:23:21', 5000000.00, 'CHODUYET'),
	('PN004', 'NCC005', 'NV004', '2026-01-14 08:23:21', 200000.00, 'DANHAP');

-- Dumping structure for table quanlytiemnet_simple.sudungdichvu
CREATE TABLE IF NOT EXISTS `sudungdichvu` (
  `MaSD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaPhien` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaDV` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SoLuong` int NOT NULL DEFAULT '1',
  `DonGia` decimal(10,2) NOT NULL COMMENT 'Giá tại thời điểm mua',
  `ThanhTien` decimal(12,2) NOT NULL COMMENT 'SoLuong * DonGia',
  `ThoiGian` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MaSD`),
  KEY `idx_maphien_sd` (`MaPhien`),
  KEY `idx_madv_sd` (`MaDV`),
  CONSTRAINT `fk_sudung_dichvu` FOREIGN KEY (`MaDV`) REFERENCES `dichvu` (`MaDV`),
  CONSTRAINT `fk_sudung_phien` FOREIGN KEY (`MaPhien`) REFERENCES `phiensudung` (`MaPhien`),
  CONSTRAINT `chk_dongia_sd` CHECK ((`DonGia` >= 0)),
  CONSTRAINT `chk_soluong` CHECK ((`SoLuong` > 0)),
  CONSTRAINT `chk_thanhtien` CHECK ((`ThanhTien` = (`SoLuong` * `DonGia`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quanlytiemnet_simple.sudungdichvu: ~8 rows (approximately)
INSERT INTO `sudungdichvu` (`MaSD`, `MaPhien`, `MaDV`, `SoLuong`, `DonGia`, `ThanhTien`, `ThoiGian`) VALUES
	('SD001', 'PS001', 'DV001', 1, 12000.00, 12000.00, '2026-01-14 08:23:21'),
	('SD002', 'PS002', 'DV003', 1, 25000.00, 25000.00, '2026-01-14 08:23:21'),
	('SD003', 'PS002', 'DV001', 1, 12000.00, 12000.00, '2026-01-14 08:23:21'),
	('SD004', 'PS003', 'DV008', 2, 15000.00, 30000.00, '2026-01-14 08:23:21'),
	('SD005', 'PS005', 'DV004', 1, 35000.00, 35000.00, '2026-01-14 08:23:21'),
	('SD006', 'PS005', 'DV002', 1, 12000.00, 12000.00, '2026-01-14 08:23:21'),
	('SD007', 'PS007', 'DV001', 1, 12000.00, 12000.00, '2026-01-14 08:23:21'),
	('SD008', 'PS009', 'DV003', 1, 25000.00, 25000.00, '2026-01-14 08:23:21');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
