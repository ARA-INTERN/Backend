package answer;

import java.util.Scanner;

// 물품 신청 정보를 표현하는 클래스
class Item1 {
    private final String applicant; // 신청자
    private final String name; // 물품명
    private final Integer quantity; // 수량
    private final Long price; // 가격
    private final String link; // 구매링크

    public Item1(String applicant, String name, Integer quantity, Long price, String link) {
        this.applicant = applicant;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.link = link;
    }

    @Override
    public String toString() {
        return String.format(
                "신청자: %s, 물품명: %s, 수량: %d, 가격: %d, 구매링크: %s",
                applicant, name, quantity, price, link
        );
    }
}

// 물품 저장소 역할을 하는 인터페이스
interface ItemStore {
    boolean save(Item1 item);
    Item1[] findAll();
    boolean isFull();
}

// 간단한 메모리 저장소 구현체
class MemoryItemStore implements ItemStore {
    private final Item1[] items;
    private Integer count = 0; // 저장된 개수

    public MemoryItemStore(Integer capacity) {
        this.items = new Item1[capacity];
    }

    @Override
    public boolean save(Item1 item) {
        if (count >= items.length) return false;
        items[count++] = item;
        return true;
    }

    @Override
    public Item1[] findAll() {
        Item1[] result = new Item1[count];
        System.arraycopy(items, 0, result, 0, count);
        return result;
    }

    @Override
    public boolean isFull() {
        return count >= items.length;
    }
}

// 입력 검증만 담당하는 클래스
class Validator {
    public static boolean isValidLink(String link) {
        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            System.out.println("올바른 링크를 입력해주세요.");
            return false;
        }
        String lower = link.toLowerCase();
        if (!(lower.contains("11st.co.kr") || lower.contains("coupang.com"))) {
            System.out.println("11번가 또는 쿠팡 링크만 가능합니다.");
            return false;
        }
        return true;
    }
}

public class StockAnswer1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ItemStore store = new MemoryItemStore(5); // 최대 5개까지 저장

        label:
        while (true) {
            System.out.println("어떤기능을 이용하실건가요?");
            System.out.println("1. 물품 신청");
            System.out.println("2. 물품 조회");
            System.out.println("3. 종료");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    if (store.isFull()) {
                        System.out.println("더 이상 신청할 수 없습니다. (최대 5개)");
                        continue;
                    }

                    System.out.println("어떤 물품을 신청하시겠습니까?");
                    System.out.print("신청자: ");
                    String applicant = scanner.nextLine().trim();

                    System.out.print("물품명: ");
                    String name = scanner.nextLine().trim();

                    Integer quantity = readInteger(scanner);
                    Long price = readLong(scanner);

                    String link;
                    do {
                        System.out.print("구매링크: ");
                        link = scanner.nextLine().trim();
                    } while (!Validator.isValidLink(link));

                    boolean success = store.save(new Item1(applicant, name, quantity, price, link));
                    if (success) {
                        System.out.println("신청이 완료되었습니다.");
                    } else {
                        System.out.println("신청 실패: 저장 공간이 가득 찼습니다.");
                    }

                    break;
                case "2":
                    Item1[] items = store.findAll();
                    if (items.length == 0) {
                        System.out.println("신청된 물품이 없습니다.");
                    } else {
                        for (Item1 item : items) {
                            System.out.println(item);
                        }
                    }

                    break;
                case "3":
                    System.out.println("프로그램을 종료합니다.");
                    break label;

                default:
                    System.out.println("잘못된 입력입니다.");
                    break;
            }
        }

        scanner.close();
    }

    private static Integer readInteger(Scanner scanner) {
        while (true) {
            System.out.print("수량: ");
            try {
                return Integer.valueOf(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }

    private static Long readLong(Scanner scanner) {
        while (true) {
            System.out.print("가격: ");
            try {
                return Long.valueOf(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
}
