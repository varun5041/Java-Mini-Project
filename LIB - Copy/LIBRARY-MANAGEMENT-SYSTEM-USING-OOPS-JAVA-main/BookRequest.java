public class BookRequest {
    int requestId;
    int memberId;
    String memberName;
    int bookId;
    String bookTitle;
    String status; // PENDING, APPROVED, REJECTED

    BookRequest(int requestId, int memberId, String memberName, int bookId, String bookTitle) {
        this.requestId = requestId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.status = "PENDING";
    }
}



