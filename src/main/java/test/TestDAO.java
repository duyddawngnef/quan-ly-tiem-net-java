package test;

import dao.DichVuDAO;
import dao.GoiDichVuDAO;
import dao.GoiDichVuKhachHangDAO;
import dao.SuDungDichVuDAO;

public class TestDAO {

    public static void main(String[] args) {

        System.out.println("=== TEST DAO START ===");

        System.out.println("\n--- DICH VU ---");
        new DichVuDAO().getAll().forEach(System.out::println);

        System.out.println("\n--- GOI DICH VU ---");
        new GoiDichVuDAO().getAll().forEach(System.out::println);

        System.out.println("\n--- GOI DICH VU KHACH HANG (KH001) ---");
        new GoiDichVuKhachHangDAO()
                .getByKhachHang("KH001")
                .forEach(System.out::println);

        System.out.println("\n--- SU DUNG DICH VU (PS001) ---");
        new SuDungDichVuDAO()
                .getByPhien("PS001")
                .forEach(System.out::println);

        System.out.println("\n=== TEST DAO END ===");
    }
}
