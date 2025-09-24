package answer;

import java.util.Scanner;

/**
 * Item 클래스
 * - 물품 신청 정보를 담는 객체
 * - 단일 책임 원칙(SRP): 물품의 속성과 문자열 표현만 담당
 */
class Item {
    private final String applicant; // 신청자
    private final String name; // 물품명
    private final Integer quantity; // 수량
    private final Long price; // 가격
    private final String link; // 구매링크

    public Item(String applicant, String name, Integer quantity, Long price, String link) {
        this.applicant = applicant;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.link = link;
    }

    // 객체 정보를 보기 좋게 문자열로 반환
    @Override
    public String toString() {
        return String.format(
                "신청자: %s, 물품명: %s, 수량: %d, 가격: %d, 구매링크: %s",
                applicant, name, quantity, price, link
        );
    }
}

/**
 * ItemRepository 클래스
 * - 데이터를 보관하고 관리하는 역할
 * - 단일 책임 원칙(SRP): Item 저장과 조회만 담당
 */
class ItemRepository {
    private final Item[] items; // 최대 저장 가능한 배열
    private Integer count = 0; // 현재 저장된 물품 개수

    public ItemRepository(Integer capacity) {
        this.items = new Item[capacity];
    }

    // 물품을 저장 (성공하면 true, 실패하면 false)
    public boolean save(Item item) {
        if (count >= items.length) return false; // 저장 공간 초과
        items[count++] = item;
        return true;
    }

    // 지금까지 저장된 모든 물품을 배열로 반환
    public Item[] findAll() {
        Item[] result = new Item[count];
        System.arraycopy(items, 0, result, 0, count);
        return result;
    }

    // 저장소가 가득 찼는지 확인
    public boolean isFull() {
        return count >= items.length;
    }
}

/**
 * ItemService 클래스
 * - 비즈니스 로직 처리
 * - 단일 책임 원칙(SRP): 물품 등록 및 조회 로직만 담당
 * - 의존 역전 원칙(DIP): Repository에 의존하지만 구체 구현 대신 추상화 개념으로 접근 가능
 */
class ItemService {
    private final ItemRepository repository; // 저장소 의존성

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    // 물품 등록 시 객체 생성 후 저장소에 위임
    public boolean registerItem(String applicant, String name, Integer quantity, Long price, String link) {
        Item item = new Item(applicant, name, quantity, price, link);
        return repository.save(item);
    }

    // 저장된 모든 물품 조회
    public Item[] getAllItems() {
        return repository.findAll();
    }

    // 저장소가 가득 찼는지 확인
    public boolean isFull() {
        return repository.isFull();
    }
}

/**
 * InputValidator 클래스
 * - 입력값 검증 전담
 * - 단일 책임 원칙(SRP): 링크 검증만 담당
 */
class InputValidator {
    // 링크가 http/https로 시작하고, 11번가/쿠팡 도메인인지 확인
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

/**
 * ItemController 클래스
 * - 사용자 입력/출력 흐름 제어
 * - 단일 책임 원칙(SRP): 메뉴 출력, 입력 처리, 서비스 호출 담당
 * - 개방-폐쇄 원칙(OCP): 기능을 쉽게 확장 가능 (예: 삭제 기능 추가)
 */
class ItemController {
    private final ItemService service; // 비즈니스 로직
    private final Scanner scanner; // 사용자 입력

    public ItemController(ItemService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    // 프로그램 메인 루프
    public void run() {
        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> handleRegister(); // 물품 신청
                case "2" -> handleView(); // 물품 조회
                case "3" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return; // 메서드 종료 → 프로그램 종료
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 메뉴 출력
    private void printMenu() {
        System.out.println("어떤기능을 이용하실건가요?");
        System.out.println("1. 물품 신청");
        System.out.println("2. 물품 조회");
        System.out.println("3. 종료");
    }

    // 물품 신청 처리
    private void handleRegister() {
        if (service.isFull()) {
            System.out.println("더 이상 신청할 수 없습니다. (최대 5개)");
            return;
        }

        System.out.println("어떤 물품을 신청하시겠습니까?");
        System.out.print("신청자: ");
        String applicant = scanner.nextLine().trim();

        System.out.print("물품명: ");
        String name = scanner.nextLine().trim();

        Integer quantity = readInteger("수량: ");
        Long price = readLong("가격: ");

        String link;
        // 링크가 올바를 때까지 반복 입력
        do {
            System.out.print("구매링크: ");
            link = scanner.nextLine().trim();
        } while (!InputValidator.isValidLink(link));

        boolean success = service.registerItem(applicant, name, quantity, price, link);
        if (success) {
            System.out.println("신청이 완료되었습니다.");
        } else {
            System.out.println("신청 실패: 저장 공간이 가득 찼습니다.");
        }
    }

    // 물품 조회 처리
    private void handleView() {
        Item[] items = service.getAllItems();
        if (items.length == 0) {
            System.out.println("신청된 물품이 없습니다.");
            return;
        }
        for (Item item : items) {
            System.out.println(item);
        }
    }

    // Integer 입력 안전 처리
    private Integer readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.valueOf(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }

    // Long 입력 안전 처리
    private Long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.valueOf(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
}

/**
 * Main 클래스
 * - 프로그램 진입점
 * - 의존 역전 원칙(DIP): Main은 구체 구현 대신 상위 계층만 알고 있음
 */
public class StockAnswer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ItemRepository repository = new ItemRepository(5); // 최대 5개까지 저장
        ItemService service = new ItemService(repository);
        ItemController controller = new ItemController(service, scanner);

        controller.run();
        scanner.close();
    }
}
