import java.util.InputMismatchException;
import java.util.Scanner;

public class Stock {
    public static void main(String[] args) {
        System.out.println("Stock Management System");
        Scanner sc = new Scanner(System.in);
        Store items = new Store();
        while (true){
            System.out.println("어떤기능을 이용하실건가요?");
            System.out.println("1. 물품 신청");
            System.out.println("2. 물품 조회");
            System.out.println("3. 종료");
            String inp = sc.nextLine();
            if (inp=="1") {
                if (items.getSize()==5) {
                    System.out.println("물품 신청은 최대 5개까지 가능합니다.");
                    continue;
                }

                System.out.println("어떤 물품을 신청하시겠습니까?");
                System.out.print("신청자: ");
                String name = sc.nextLine();

                System.out.print("물품명: ");
                String item = sc.nextLine();

                Integer quantity = input(sc, "수량");

                Integer price = input(sc, "가격");

                String link;
                do {
                    System.out.print("구매링크: ");
                    link = sc.nextLine();
                } while (!Checker.check(link));
                Item i = new Item(name, item, quantity, price, link);
                items.saveItem(i);
                System.out.println("성공적으로 신청되었습니다.");
            } else if (inp=="2") {
                Item[] i = items.getItem();
                if (i.length == 0) {
                    System.out.println("신청된 물품이 없습니다.");
                } else {
                    for(Item item : i){
                        System.out.println(item.getItem());
                    }
                }
            } if (inp=="3") {
                System.out.println("프로그램을 종료합니다.");
                break;
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
        sc.close();
    }

    private static Integer input(Scanner sc, String st) {
        while (true){
            System.out.printf("%s: ", st);
            try {
                Integer i = sc.nextInt();
                sc.nextLine();
                return i;
            } catch(InputMismatchException e){
                System.out.println("입력이 올바르지 않습니다.");
                sc.nextLine();
            }
        }
    }
}

class Item{
    private final String name;
    private final String item;
    private final Integer quantity;
    private final Integer price;
    private final String link;

    public Item(String name, String item, Integer quantity, Integer price, String link) {
        this.name = name;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.link = link;
    }

    public String getItem() {
        return String.format("신청자: %s, 물품명: %s, 수량: %d, 가격: %d, 구매링크: %s", name, item, quantity, price, link);
    }
}

class Checker {
    public static boolean check(String link){
        if (!link.startsWith("http://") && !link.startsWith("https://")){
            System.out.println("올바른 링크를 입력해주세요.");
            return false;
        } else if (!link.contains("11st.co.kr") && !link.contains("coupang.com")) {
            System.out.println("11번가 또는 쿠팡 링크만 가능합니다.");
            return false;
        } else {
            return true;
        }
    }
}

class Store {
    private final Item[] items = new Item[5];
    private Integer size = 0;

    public void saveItem(Item item) {
        if (size<5) items[size++] = item;
        else System.out.println("물품 신청은 최대 5개까지 가능합니다.");
    }

    public Item[] getItem() {
        Item[] temp = new Item[size];
        System.arraycopy( items, 0, temp, 0, size );
        return temp;
    }

    public Integer getSize() {
        return size;
    }
}