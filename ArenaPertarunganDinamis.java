import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class ArenaPertarunganDinamis {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ArrayList<Musuh> gelombangMonster = new ArrayList<>();
        gelombangMonster.add(new Slime());
        gelombangMonster.add(new Naga());
        gelombangMonster.add(new Slime());
        gelombangMonster.add(new Zombie());

        System.out.println("\n=====================================");
        System.out.println("   ARENA RPG: GELOMBANG MONSTER ");
        System.out.println("=====================================");
        System.out.println("AWAS! Sekelompok monster menghadang Anda!");

        boolean isBermain = true;

        while (isBermain && !gelombangMonster.isEmpty()) {
            System.out.println("\n------ STATUS MONSTER ------ ");

            for (int i = 0; i < gelombangMonster.size(); i++) {
                Musuh m = gelombangMonster.get(i);
                System.out.println((i + 1) + ". " + m.namaMusuh + " (HP: " + m.healthPoint + ");");
            }
            System.out.println("----------------------------");
            System.out.println("8. [SIMPAN GAME] simpan Progress Game");
            System.out.println("9. [LOAD GAME] Muat Progress Game");
            System.out.println("0. Kabur dari pertarungan");
            System.out.print("\npilih target monster (1-" + gelombangMonster.size() + ") atau aksi lainnya: ");

            try {
                int pilihanTarget = input.nextInt();
                if (pilihanTarget == 0) {
                    System.out.println("Cupu bet lu lari terbirit-birit");
                    isBermain = false;
                    continue;

                } else if (pilihanTarget == 8) {
                    try (ObjectOutputStream oos = new ObjectOutputStream(
                            new FileOutputStream("savegame_rpg.dat"))) {

                        oos.writeObject(gelombangMonster);
                        System.out.println("[SISTEM] Game berhasil disimpan!");
                    } catch (IOException e) {
                        System.out.println("[ERROR] Terjadi kesalahan saat menyimpan game: " + e.getMessage());
                    }
                    continue;
                } else if (pilihanTarget == 9) {
                    try (ObjectInputStream ois = new ObjectInputStream(
                            new FileInputStream("savegame_rpg.dat"))) {

                        gelombangMonster = (ArrayList<Musuh>) ois.readObject();
                        System.out.println("<<BERHASIL>> Progress permainan berhasil dimuat!");
                    } catch (ClassNotFoundException e) {
                        System.out.println("<<GAGAL>> file save game Tidak ada. coba Memulai game baru.");
                    } catch (IOException e) {
                        System.out.println("<<GAGAL>> terjadi kesalahan saat memuat game: " + e.getMessage());
                    }
                    continue;
                }

                if (pilihanTarget < 1 || pilihanTarget > gelombangMonster.size()) {
                    System.out.println("MATA LUU PICEKKK YAA :D");
                    continue;
                }

                int indeksMonster = pilihanTarget - 1;
                Musuh target = gelombangMonster.get(indeksMonster);

                System.out.print("Masukin kekuatan serangan lu (10 - 100): ");
                int power = input.nextInt();

                if (power < 10 || power > 100) {
                    throw new SeranganTidakValidException("-literasi ya, HARUS (10-100)!!!");
                }

                System.out.println("\n>>> HASIL SERANGAN ANDA <<<");
                target.terimaDamage(power);

                if (target.healthPoint <= 0) {
                    System.out.println(target.namaMusuh + " MATII DIA");
                    if (target instanceof DropItem) {
                        DropItem Loot = (DropItem) target;
                        Loot.dropItem();
                        gelombangMonster.remove(indeksMonster);
                    }
                }
                System.out.println("\n<<< GILIRAN MONSTER MEMBALAS >>>");

                for (int i = 0; i < gelombangMonster.size(); i++) {
                    if (gelombangMonster.get(i).healthPoint > 0) {
                        Musuh monsterAktif = gelombangMonster.get(i);
                        monsterAktif.suaraKhas();

                        if (monsterAktif instanceof BisaTerbang) {
                            System.out.println("[PERINGATAN! SERANGAN UDARA TERDETEKSI]");
                            BisaTerbang monsterTerbang = (BisaTerbang) monsterAktif;
                            monsterTerbang.lepasLandas();
                            monsterTerbang.seranganUdara();
                        } else {
                            monsterAktif.serangPemain();
                        }
                    }
                }
                System.out.println("\n-----------------------------------------------------------------------------");

            } catch (Exception e) {
                System.out.println("Terjadi kesalahan sistem: " + e.getMessage());
                input.nextLine();
                continue;
            }
            if (gelombangMonster.isEmpty()) {
                System.out.println("\nSemua monster telah dikalahkan!");
                break;
            }
        }

        boolean semuaMati = true;
        for (int i = 0; i < gelombangMonster.size(); i++) {
            if (gelombangMonster.get(i).healthPoint > 0) {
                semuaMati = false;
                break;
            }
        }

        if (semuaMati) {
            System.out.println("\nya ya congrats, lu menang.");
            isBermain = false;
        }

        input.close();
        System.out.println("\nGame Over.");
    }
}