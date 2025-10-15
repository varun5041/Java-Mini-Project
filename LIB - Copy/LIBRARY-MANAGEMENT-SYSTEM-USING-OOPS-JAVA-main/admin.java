

import java.util.ArrayList;
import java.util.Scanner;
public class admin 
{
    Scanner sc =new Scanner(System.in);
    String Adminname;
    int AdminId;
    String password = "admin";
     
    //admin constructor
    admin(String name,int id)
    {
        this.Adminname=name;
        this.AdminId=id;
    }

    //admin have book list and members list
    ArrayList<Book> books = new ArrayList<>(); 
    ArrayList<member> memberslist = new ArrayList<>();
    ArrayList<BookRequest> requests = new ArrayList<>();


    //display all books in library
    public void showBooks() 
    {
        if (books.isEmpty()) 
        {
            System.out.println("No books available.");
        } 

        else 
        {
            for (Book i : books) 
            {
                System.out.println(i.toString());
            }
        }
    }
    
    //add a book by id
    public void addbook(int bookidnum,String booktitle,String bookAuthor,double price)
    {
        books.add(new Book(bookidnum,booktitle,bookAuthor,price));
        System.out.println("BOOK ADDED!");
    }

    //remove a book by id
    public void removebook() 
    {
        System.out.println("ENTER BOOK ID TO REMOVE:");
        int bookidtoremove = sc.nextInt();
    
        for (Book i : books) 
        {
            if (bookidtoremove == i.BookId) 
            {
                books.remove(i);
                System.out.println("Book removed successfully!");
                    return; 
            }
        }

        
        System.out.println("BOOK NOT FOUND!");
    }

     
    
    public void AddMember(int Id,String name)
    {
        memberslist.add(new member(Id,name));
    } 

    
   public void removemember(int Id) {
   int removememberid=Id;
    
    for (member i : memberslist) {
        if (removememberid == i.MemberId) {
            memberslist.remove(i);
            System.out.println("Member removed successfully!");
            return; 
        }
    }
    
    
    System.out.println("Member not found!");
}


    
    public void showmembers()
    {
        if(memberslist.isEmpty())
        {
            System.out.println("THERE ARE NO MEMBERS IN THE LIBRARY!");
        }
        else
        {
            for(member i : memberslist)
            {
                System.out.println("memberid:" + i.MemberId + " MEMBER name "+ i.Membername );
            }
        }   
    }

	// search by title
	public ArrayList<Book> searchBooksByTitle(String query)
	{
		ArrayList<Book> result = new ArrayList<>();
		if (query == null) return result;
		String q = query.toLowerCase();
		for (Book b : books)
		{
			if (b.Title != null && b.Title.toLowerCase().contains(q))
			{
				result.add(b);
			}
		}
		return result;
	}

	// search by author
	public ArrayList<Book> searchBooksByAuthor(String query)
	{
		ArrayList<Book> result = new ArrayList<>();
		if (query == null) return result;
		String q = query.toLowerCase();
		for (Book b : books)
		{
			if (b.Author != null && b.Author.toLowerCase().contains(q))
			{
				result.add(b);
			}
		}
		return result;
	}

	// issue book
	public boolean issueBookToMember(int bookId, int memberId)
	{
		Book foundBook = null;
		for (Book b : books)
		{
			if (b.BookId == bookId)
			{
				foundBook = b;
				break;
			}
		}
		if (foundBook == null || !foundBook.isAvailable()) return false;

		member foundMember = null;
		for (member m : memberslist)
		{
			if (m.MemberId == memberId)
			{
				foundMember = m;
				break;
			}
		}
		if (foundMember == null) return false;

		foundBook.markIssuedTo(memberId);
		foundMember.recievebook(foundBook);
		return true;
	}

	// return book
	public boolean returnBookFromMember(int bookId, int memberId)
	{
		Book foundBook = null;
		for (Book b : books)
		{
			if (b.BookId == bookId)
			{
				foundBook = b;
				break;
			}
		}
		if (foundBook == null || foundBook.isAvailable()) return false;
		if (foundBook.IssuedToMemberId == null || foundBook.IssuedToMemberId != memberId) return false;

		member foundMember = null;
		for (member m : memberslist)
		{
			if (m.MemberId == memberId)
			{
				foundMember = m;
				break;
			}
		}
		if (foundMember == null) return false;

		foundMember.givebook(foundBook);
		foundBook.markReturned();
		return true;
	}

	public member getMemberById(int id)
	{
		for (member m : memberslist)
		{
			if (m.MemberId == id) return m;
		}
		return null;
	}

	public ArrayList<Book> getBooks()
	{
		return books;
	}

	public ArrayList<member> getMembers()
	{
		return memberslist;
	}

	public ArrayList<BookRequest> getRequests()
	{
		return requests;
	}

	public BookRequest submitRequest(int memberId, int bookId, String title)
	{
		member m = getMemberById(memberId);
		if (m == null) return null;
		int rid = requests.size() + 1;
		BookRequest req = new BookRequest(rid, memberId, m.Membername, bookId, title);
		requests.add(req);
		return req;
	}

	public boolean approveRequest(int requestId)
	{
		for (BookRequest r : requests)
		{
			if (r.requestId == requestId && "PENDING".equals(r.status))
			{
				boolean ok = issueBookToMember(r.bookId, r.memberId);
				r.status = ok ? "APPROVED" : "REJECTED";
				return ok;
			}
		}
		return false;
	}

	public boolean rejectRequest(int requestId)
	{
		for (BookRequest r : requests)
		{
			if (r.requestId == requestId && "PENDING".equals(r.status))
			{
				r.status = "REJECTED";
				return true;
			}
		}
		return false;
	}
}
