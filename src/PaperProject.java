import java.util.Scanner;

public class PaperProject {

    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        menu();
        System.out.print("Goodbye :)");
        scanner.close();
    }

    public static void menu() {
        int id = -1;
        System.out.println("This is the paper database.");
        PaperArray paperDB = new PaperArray();
        paperDB.papersArray = new Paper[7];
        paperDB.papersArray[0] = createPaper("Brunnbauer", "b-title", 2014, 5, 30, 2, ++id);
        paperDB.papersArray[1] = createPaper("Cerny", "a-title", 2020, 3, 13, 3, ++id);
        paperDB.papersArray[2] = createPaper("Demetz", "r-title", 1990, 1, 7 , 88, ++id);
        paperDB.papersArray[3] = createPaper("Hösel", "R-title", 1999, 12, 31, 2, ++id);
        paperDB.papersArray[4] = createPaper("Mandl", "g-title", 2018, 7, 6, 5, ++id);
        paperDB.papersArray[5] = createPaper("Satek", "z-title", 2004, 8, 27, 12, ++id);
        paperDB.papersArray[6] = createPaper("Brunnbauer", "l-title", 2004, 8, 17, 2, ++id);
        for (int i = 0; i < paperDB.papersArray.length; i++) {
            paperDB.papersArray[i].weekday = weekdayString(paperDB, i);
        }

        paperDB.papersArray = addReference(paperDB, 0, new int[] {1, 4, 3, 2});
        paperDB.papersArray = addReference(paperDB, 1, new int[] {0});
        paperDB.papersArray = addReference(paperDB, 2, new int[] {0});
        paperDB.papersArray = addReference(paperDB, 3, new int[] {1, 0});
        paperDB.papersArray = addReference(paperDB, 4, new int[] {1, 0});
        paperDB.papersArray = addReference(paperDB, 5, new int[] {1, 0});
        for (int i = 0; i < paperDB.papersArray.length; i++) {
            paperDB.papersArray[i].refs = sortList(paperDB.papersArray[i].refs, paperDB);
        }
        printTable(paperDB);
        System.out.println("Following options are available to manipulate the database:");
        int number;
        do {
            System.out.println("How would you like to manipulate the database?");
            System.out.println("To add a new paper, enter 1");
            System.out.println("To display papers, enter 2");
            System.out.println("To delete a paper, enter 3");
            System.out.println("To reference a link, enter 4");
            System.out.println("To search the database, enter 5");
            System.out.println("To sort the database, enter 6");
            System.out.println("To perform a statistic analysis, enter 7");
            System.out.println("To exit the program, enter 8");
            number = scanner.nextInt();
            switch (number) {
                case 1:
                    id += 1;
                    paperDB = addingPaper(id, paperDB);
                    printTable(paperDB);
                    break;
                case 2:
                    System.out.println("Display paper:");
                    print(paperDB);
                    break;
                case 3:
                    if (paperDB.papersArray.length == 0) {
                        System.out.println("There are no entries in the database please add a paper to be able to delete again.");
                    } else {
                        System.out.println("Delete a paper:");
                        printTable(paperDB);
                        System.out.println("Which entry would you like to delete?");
                        int deleteIndex = scanner.nextInt();
                        while (deleteIndex < 1 || deleteIndex > paperDB.papersArray.length) {
                            System.out.println("This entry is not contained in the database. Please reenter");
                            deleteIndex = scanner.nextInt();
                        }
                        paperDB.papersArray = deleteReferences(paperDB, deleteIndex - 1);
                        paperDB.papersArray = deletePaper(paperDB, deleteIndex - 1);
                        for (int i = 0; i < paperDB.papersArray.length; i++) {
                            paperDB.papersArray[i].refs = sortList(paperDB.papersArray[i].refs, paperDB);
                        }
                        printTable(paperDB);
                    }
                    break;
                case 4:
                    if (paperDB.papersArray.length <= 1) {
                        System.out.println("There are not enough entries in the database please add new paper(s) and retry");
                    } else {
                        boolean unAbleToAdd = true;
                        for (int i = 0; i < paperDB.papersArray.length; i++) {
                            if (countNumberOfReferences(paperDB.papersArray[i].refs) == paperDB.papersArray.length - 1){
                                unAbleToAdd = true;
                            } else {
                                unAbleToAdd = false;
                                break;
                            }
                        }
                        if (!unAbleToAdd) {
                            System.out.println("Link references:");
                            System.out.println("Select a paper that you would like to add references to, here are the choices to choose from (please always enter the entry nr.):");
                            printReferences(paperDB);
                            int paperToReferenceTo = scanner.nextInt();
                            while (paperToReferenceTo > paperDB.papersArray.length || paperToReferenceTo < 1 || !canThisOneBeReferencedTo(paperDB.papersArray[paperToReferenceTo - 1].refs, paperDB)) {
                                System.out.println("Paper number must in the range of papers given, or all other papers have already been referenced in this paper. Repeat entry");
                                paperToReferenceTo = scanner.nextInt();
                            }
                            paperToReferenceTo -= 1;
                            System.out.println("Theses papers can still reference to the already chosen paper, without being referenced twice");
                            printWhichReferencesCanStillLinkToThis(paperDB, paperToReferenceTo);
                            int maxArrayLength = howManyCanBeReferenced(paperDB.papersArray[paperToReferenceTo].refs, paperDB);
                            System.out.println("Please enter how many papers, the chosen paper should reference, must be one or more.");
                            int amountOfReferences = scanner.nextInt();
                            while (amountOfReferences < 1 || amountOfReferences >= paperDB.papersArray.length || amountOfReferences > maxArrayLength) {
                                System.out.printf("You must enter at least one but cannot exceed %d entries\n", maxArrayLength);
                                System.out.println("You cannot refer a paper twice to this paper maybe this paper cannot refer this amount of papers without double referring! Please reenter");
                                amountOfReferences = scanner.nextInt();
                            }

                            int[] referencesToArray = new int[amountOfReferences];
                            System.out.println("Now enter which papers you would you like reference in the chosen paper. You must enter the amount of papers you chose: ");
                            int referenceTo;
                            for (int i = 0; i < amountOfReferences; i++) {
                                referenceTo = scanner.nextInt() - 1;
                                while (referenceTo < 0 || referenceTo == paperToReferenceTo || referenceTo > paperDB.papersArray.length - 1 || listContains(paperDB.papersArray[paperToReferenceTo].refs, paperDB.papersArray[referenceTo].id)) {
                                    System.out.println("This is not a valid input. Paper must be a different to the one you are referencing to and a paper cannot be referenced twice. Repeat entry");
                                    referenceTo = scanner.nextInt();
                                    referenceTo -= 1;
                                }
                                referencesToArray[i] = referenceTo;
                            }
                            int index = paperToReferenceTo;
                            ListNode head = paperDB.papersArray[index].refs;
                            for (int i = 0; i < referencesToArray.length; i++) {
                                head = addFront(head, paperDB.papersArray[referencesToArray[i]].id);
                                System.out.printf("adding %s to %s\n\n", paperDB.papersArray[referencesToArray[i]].author + paperDB.papersArray[referencesToArray[i]].publicationDate.year, paperDB.papersArray[index].author + paperDB.papersArray[index].publicationDate.year);
                            }
                            paperDB.papersArray[index].refs = head;
                            for (int i = 0; i < paperDB.papersArray.length; i++) {
                                paperDB.papersArray[i].refs = sortList(paperDB.papersArray[i].refs, paperDB);
                            }
                        } else {
                            System.out.printf("All papers have been referenced to by other papers, add more papers to be able to reference again\n\n");
                        }
                    }
                    break;
                case 5:
                    System.out.println("Search database:");
                    printTable(paperDB);
                    System.out.println("Categories in which you can search: \nTo search for an author, enter 1");
                    System.out.println("To search for a title, enter 2");
                    System.out.println("To search for a date, enter 3");
                    System.out.println("To search for an amount of pages, enter 4");
                    System.out.println("To search for a reference title, enter 5");
                    System.out.println("To search for a publication weekday, enter 6");
                    int searchNumber = scanner.nextInt();
                    while (searchNumber < 1 || searchNumber > 6) {
                        System.out.println("Please reenter search category.");
                        searchNumber = scanner.nextInt();
                    }
                    switch (searchNumber) {
                        case 1:
                            System.out.println("Which author would you like to search for? \nEnter a name:");
                            String authorName = scanner.nextLine();
                            authorName = scanner.nextLine();
                            authorSearch(paperDB, authorName);
                            break;
                        case 2:
                            System.out.println("Which title would you like to search for? \nEnter a title:");
                            String titleName = scanner.nextLine();
                            String title;
                            title = scanner.nextLine();
                            titleSearch(paperDB, title);
                            break;
                        case 3:
                            System.out.println("Which date would you like to search for? \nEnter a date:");
                            int dateDay = publicationDate();
                            dateSearch(paperDB,dateDay);
                            break;
                        case 5:
                            System.out.println("Which reference would you like to search for?\nEnter a title:");
                            String referenceName = scanner.nextLine();
                            referenceName = scanner.nextLine();
                            referenceSearch(paperDB, referenceName);
                            break;
                        case 4:
                            System.out.println("For what amount of pages would you like to search for?\nEnter a number of pages");
                            int amountOfPages = scanner.nextInt();
                            while (amountOfPages < 0) {
                                System.out.println("A paper must have one page or more. Please reenter the amount of pages.");
                                amountOfPages = scanner.nextInt();
                            }
                            pagesSearch(paperDB, amountOfPages);
                            break;
                        case 6:
                            System.out.println("For what weekday would you like to search?\nEnter a Weekday");
                            String weekday = scanner.next();
                            weekdaySearch(paperDB, weekday);
                            break;
                        default:
                            break;
                    }
                    System.out.println();
                    break;
                case 6:
                    System.out.println("Sort database:");
                    System.out.println("Categories in which the database can be sorted: \nTo arrange the database according to the author, enter 1");
                    System.out.println("To arrange the database according to the title, enter 2");
                    System.out.println("To arrange the database according to the publishing date, enter 3");
                    int sortNumber = scanner.nextInt();
                    while (sortNumber < 1 || sortNumber > 3) {
                        System.out.println("Invalid input. Reenter the sort option");
                        sortNumber = scanner.nextInt();
                    }
                    switch (sortNumber) {
                        case 1:
                            sortAuthor(paperDB);
                            printTable(paperDB);
                            break;
                        case 2:
                            sortTitle(paperDB);
                            printTable(paperDB);
                            break;
                        case 3:
                            sortDate(paperDB);
                            printTable(paperDB);
                            break;
                        default:
                            break;
                    }
                    System.out.println();
                    break;
                case 7:
                    System.out.println("Performing statistical analysis:");
                    statisticalAnalysis(paperDB);
                    break;
                case 8:
                    break;
                default:
                    System.out.println("Invalid input! Please enter another number.");
                    break;
            }
        } while (number != 8);
        System.out.println("Exiting program.");
    }


    public static int publicationDate() {
        int year, month, day, date;
        boolean isLeapYear = false;
        boolean isThisYear = false;
        System.out.println("Enter the publication year: (Please note that once a year is entered it cannot be corrected)");
        while(true) {
            year = scanner.nextInt();
            if (year % 4 == 0) {
                isLeapYear = true;
                if (year % 100 == 0) {
                    isLeapYear = false;
                    if (year % 400 == 0) {
                        isLeapYear = true;
                    }
                }
            }
            if (year >= 2022) {
                System.out.println("We are not in this year yet. Please repeat the publication year:");
            } else if (year == 2021) {
                isThisYear = true;
                break;
            } else {
                break;
            }
        }
        System.out.println("Enter the publication month: (Please note that once a month is entered it cannot be corrected)");
        while (true) {
            month = scanner.nextInt();
            if (isThisYear && (month > 3 || month < 1)) {
                System.out.println("This month is not in this current year. Please repeat the publication month:");
            } else if (month > 12 || month < 1) {
                System.out.println("This month is not in any year. Please repeat the month");
            } else {
                break;
            }
        }
        System.out.println("Enter the publication day: (Please note that once a day is entered it cannot be corrected)");
        while (true) {
            day = scanner.nextInt();
            if (isLeapYear && month == 2 && day <= 39) {
                break;
            } else if (isThisYear && month == 3 && day == 1) {
                break;
            } else if (isLeapYear && month == 1 && day < 32) {
                break;
            } else if (day <= 31 && (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) {
                break;
            } else if (day <= 30 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                break;
            } else if (!isLeapYear && month == 2 && day <= 28) {
                break;
            } else {
                System.out.println("This day is in the future or not in a month, please renter the publication day:");
            }
        }
        date = year * 10000 + month * 100 + day;
        return date;
    }
    public static PaperArray addingPaper(int id, PaperArray paperDB) {
        int lengthOfArray = paperDB.papersArray.length + 1;
        Paper[] copyArray = new Paper[paperDB.papersArray.length];
        for (int i = 0; i < paperDB.papersArray.length; i++) {
            copyArray[i] = paperDB.papersArray[i];
        }
        paperDB.papersArray = new Paper[lengthOfArray];
        for (int i = 0; i < lengthOfArray - 1; i++) {
            paperDB.papersArray[i] = copyArray[i];
        }
        paperDB.papersArray[lengthOfArray - 1] = scanningPaper(id);
        paperDB.papersArray[lengthOfArray - 1].weekday = weekdayString(paperDB, lengthOfArray - 1);
        return paperDB;
    }
    public static Paper scanningPaper (int id) {
        System.out.println("Create a new paper:");
        System.out.println("Enter required data to create a new paper");
        String author = scanner.nextLine();
        String correctAuthor = "^([A-Z]+[\\S]+)(\\s[A-Z]*[\\S]*)*$";
        System.out.println("Enter author name:");
        while (true) {
            author = scanner.nextLine();
            if (author.matches(correctAuthor)) {
                break;
            } else {
                System.out.println("Repeat author name, it must have a capital letter and be followed by at least one more lowercase letter.");
            }
        }
        String title;
        String correctTitle = "^([\\S]+)([\\s]*[\\S]*)*$";
        System.out.println("Enter title:");
        while (true) {
            title = scanner.nextLine();
            if (title.matches(correctTitle)) {
                break;
            } else {
                System.out.println("Repeat the title, it must consist of at least one regular expression.");
            }
        }
        int date = publicationDate();
        int year = date / 10000;
        int month = (date / 100) % 100;
        int day = date % 100;
        int pages;
        System.out.println("Enter the number of pages: ");
        while (true) {
            pages = scanner.nextInt();
            if (pages <= 0) {
                System.out.println("Repeat entry, the number of pages must be lager than 0:");
            } else {
                break;
            }
        }
        return createPaper(author, title, year, month, day, pages, id);
    }
    public static Paper createPaper(String author, String title, int year, int month, int day, int pages, int id) {
        Paper paper = new Paper();
        paper.publicationDate = new Date();
        paper.author = author;
        paper.title = title;
        paper.publicationDate.year = year;
        paper.publicationDate.month = month;
        paper.publicationDate.day = day;
        paper.pages = pages;
        paper.id = id;
        return paper;
    }

    public static void print(PaperArray array) {
        System.out.println("How would you like to print?");
        System.out.println("Options are: \nTo print a single entry in line format, enter 1");
        System.out.println("To print a single entry in short format, enter 2");
        System.out.println("To print a single entry in detailed format, enter 3");
        System.out.println("To print all entries in a table format, enter 4");
        int format = scanner.nextInt();
        int entry;
        while (format < 1 || format > 4) {
            System.out.println("Please reenter wished print format!");
            format = scanner.nextInt();
        }
        switch (format) {
            case 1:
                System.out.println("Line format");
                System.out.printf("Which paper details would you like to print? The database has %d entries. \n", array.papersArray.length);
                entry = scanner.nextInt();
                while (entry < 1 || entry > array.papersArray.length) {
                    System.out.println("This is not contained in the database please reenter which paper details you would like to print.");
                }
                entry -= 1;
                printLine(array, entry);
                break;
            case 2:
                System.out.println("Short format: ");
                System.out.printf("Which paper details would you like to print? The database has %d entries.\n", array.papersArray.length);
                entry = scanner.nextInt();
                while (entry < 1 || entry > array.papersArray.length) {
                    System.out.println("This is not contained in the database please reenter which paper details you would like to print.");
                }
                entry -= 1;
                printShort(array, entry);
                break;
            case 3:
                System.out.println("Detailed format: ");
                System.out.printf("Which paper details would you like to print? The database has %d entries.\n", array.papersArray.length);
                entry = scanner.nextInt();
                while (entry < 1 || entry > array.papersArray.length) {
                    System.out.println("This is not contained in the database please reenter which paper details you would like to print.");
                }
                entry -= 1;
                printDetailed(array, entry);
                break;
            case 4:
                System.out.println("Table line format: ");
                printTable(array);
                break;
            default:
                break;
            }
    }
    public static void printLine(PaperArray array, int index) {
        System.out.printf("%-10s", index + 1);
        System.out.printf("%-20s ", array.papersArray[index].author);
        System.out.printf("%-15s ", array.papersArray[index].title);
        System.out.printf("%-10d", array.papersArray[index].id);
        System.out.printf("%04d-%02d-%02d    ", array.papersArray[index].publicationDate.year, array.papersArray[index].publicationDate.month, array.papersArray[index].publicationDate.day);
        System.out.printf("%-15s", array.papersArray[index].weekday);
        System.out.printf("%4d ", array.papersArray[index].pages);
        System.out.printf("  %d \n\n", countNumberOfReferences(array.papersArray[index].refs));
    }
    public static void printShort(PaperArray array, int index) {
        System.out.printf("%-20s ", array.papersArray[index].author);
        System.out.printf("%-15s \n\n", array.papersArray[index].title);
    }
    public static void printDetailed(PaperArray array, int index) {
        System.out.printf("    Author: %s\n", array.papersArray[index].author);
        System.out.printf("     Title: %s\n", array.papersArray[index].title);
        System.out.printf("  Paper ID: %-10d\n", array.papersArray[index].id);
        System.out.printf("      Date: %04d-%02d-%02d\n", array.papersArray[index].publicationDate.year, array.papersArray[index].publicationDate.month, array.papersArray[index].publicationDate.day);
        System.out.printf("   Weekday: %-15s\n", array.papersArray[index].weekday);
        System.out.printf("     Pages: %s\n", array.papersArray[index].pages);
        System.out.printf("References: %d \n", countNumberOfReferences(array.papersArray[index].refs));
        for (ListNode cur = array.papersArray[index].refs; cur != null; cur = cur.next) {
            for (int i = 0; i < array.papersArray.length; i++) {
                if (cur.data == array.papersArray[i].id) {
                    System.out.printf("\t[%2d]: %-20s %d\n", i ,array.papersArray[i].author, array.papersArray[i].publicationDate.year);
                }
            }
        }
        System.out.println();
    }
    public static void printTable(PaperArray array) {
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s", "entry nr.");
        System.out.printf("%-20s ", "Author");
        System.out.printf("%-15s ", "Title");
        System.out.printf("%-10s", "Paper ID");
        System.out.printf("%-14s ", "Pub.Date");
        System.out.printf("%-15s", "Weekday");
        System.out.printf("%-7s ", "Pages");
        System.out.printf("%-6s\n", "Refs");
        System.out.println("--------- -------------------- --------------- --------- -------------- -------------- ------- ----");
        for (int i = 0; i < array.papersArray.length; i++) {
            System.out.printf("%-10s", i + 1);
            System.out.printf("%-20s ", array.papersArray[i].author);
            System.out.printf("%-15s ", array.papersArray[i].title);
            System.out.printf("%-10d", array.papersArray[i].id);
            System.out.printf("%04d-%02d-%02d     ", array.papersArray[i].publicationDate.year, array.papersArray[i].publicationDate.month, array.papersArray[i].publicationDate.day);
            System.out.printf("%-15s", array.papersArray[i].weekday);
            System.out.printf("%7s ", array.papersArray[i].pages);
            int refs = countNumberOfReferences(array.papersArray[i].refs);
            System.out.printf("%4d\n", refs);
        }
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf("%d element(s).\n", array.papersArray.length);
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public static String weekdayString(PaperArray array, int i) {
        int date = array.papersArray[i].publicationDate.year * 10000 + array.papersArray[i].publicationDate.month * 100 + array.papersArray[i].publicationDate.day;
        int day = date % 100, month = date % 10000 / 100, year = date / 10000;
        String hello = "n.a.";
        if (validate(year, month, day)) {
            int W = weekday(day, month, year);
            hello = dayName(W);
        }
        return hello;
    }
    public static boolean validate(int year) {
        return year >= 1582 && year <= 2199;
    }
    public static boolean validate(int year, int month) {
        if (year == 1582 && month <= 9) {
            return false;
        } else {
            return year <= 2199 && year > 1581 && month > 0 && month < 13;
        }
    }
    public static boolean isLeap(int inYear) {
        boolean isALeapYear = false;
        if (inYear % 4 == 0) {
            isALeapYear = true;
            if (inYear % 100 == 0) {
                isALeapYear = false;
                if (inYear % 400 == 0) {
                    isALeapYear = true;
                }
            }
        }
        return isALeapYear;
    }
    public static int nDays(int month, int year) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if (isLeap(year)) {
            return 29;
        } else {
            return 28;
        }
    }
    public static boolean validate(int year, int month, int day) {
        boolean noooo;
        if (year == 1582 && month == 10 && day <= 16) {
            noooo = false;
            return noooo;
        }
        noooo = validate(year) && validate(year, month) && day <= nDays(month, year);
        return noooo;
    }
    public static int weekday(int inDay, int inMonth, int inYear) {
        int[] array = new int[13];
        array [0] = 0;
        int plah = 3;
        for (int i = 1; i < array.length; i++) {
            if (plah == 13) {
                plah = 1;
            }
            array[i] = plah;
            plah++;
        }
        for (int i = 0; i < array.length; i++) {
            if (inMonth == array[i]) {
                inMonth = i;
                i = 12;
            }
        }
        if (inMonth >= 11) {
            inYear -= 1;
        }
        double two = 2.6 * inMonth - 0.2;
        int one = (int) two;
        int a = inDay + one + (inYear % 100) + ((inYear % 100) / 4) + ((inYear / 100) / 4) - 2*(inYear / 100);
        if (a % 7 < 0) {
            return (a % 7) + 7;
        } else {
            return a % 7;
        }
    }
    public static String dayName(int W) {
        String[] array2 = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int a = 0;
        for (int i = 0; i < array2.length; i++) {
            if (i == W) {
                a = i;
                i = 7;
            }
        }
        return array2[a];
    }

    public static int countNumberOfReferences(ListNode head) {
        int number = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            number++;
        }
        return number;
    }
    public static Paper[] deletePaper(PaperArray array, int deleteIndex) {
        array.papersArray[deleteIndex] = null;
        int a = 0;
        Paper[] returnArray = new Paper[array.papersArray.length - 1];
        for (int i = 0; i < returnArray.length; i++) {
            a += 1;
            if (i >= deleteIndex) {
                returnArray[i] = array.papersArray[a];
            } else {
                returnArray[i] = array.papersArray[i];
            }
        }
        return returnArray;
    }
    public static Paper[] deleteReferences(PaperArray array, int deleteIndex) {
        int id = array.papersArray[deleteIndex].id;
        for (int i = 0; i < array.papersArray.length; i++) {
            if (i == deleteIndex && deleteIndex < array.papersArray.length - 1) {
                i += 1;
            }
            if (i == deleteIndex && deleteIndex == array.papersArray.length - 1) {
                break;
            }
            array.papersArray[i].refs = deleteRefs(array.papersArray[i].refs, id);
        }
        return array.papersArray;
    }
    public static ListNode deleteRefs(ListNode head, int id) {
        boolean isId = false;
        ListNode giveBack = null;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.data == id) {
                isId = true;
            }
        }
        if (isId) {
            int length = countNumberOfReferences(head);
            int[] a = new int[length];
            int i = 0;
            for (ListNode cur = head; cur != null; cur = cur.next) {
                a[i++] = cur.data;
            }
            giveBack = backToList(head, id, a);
        } else {
            return head;
        }
        return giveBack;
    }
    public static ListNode backToList(ListNode head, int id, int[] arr) {
        ListNode backList = null;
        int length = arr.length;
        int index = 0;
        while (index < length) {
            if (arr[index] == id) {
                index += 1;
            } else {
                backList = addFront(backList, arr[index]);
                index += 1;
            }
        }
        return backList;
    }

    public static void authorSearch(PaperArray array, String authorName) {
        boolean matches = false;
        for (int i = 0; i < array.papersArray.length; i++) {
            if (array.papersArray[i].author.compareToIgnoreCase(authorName) == 0)  {
                matches = true;
                printLine(array, i);
            }
        }
        if (!matches) {
            System.out.println("There was no paper found with this author.");
        }
    }
    public static void titleSearch(PaperArray array, String titleName) {
        boolean matches = false;
        for (int i = 0; i < array.papersArray.length; i++) {
            if (array.papersArray[i].title.compareToIgnoreCase(titleName) == 0) {
                matches = true;
                printLine(array, i);
            }
        }
        if (!matches) {
            System.out.println("There was no paper found with this title.");
        }
    }
    public static void dateSearch(PaperArray array, int date) {
        boolean state = false;
        int year = date / 10000;
        int month = (date / 100) % 100;
        int day = date % 100;
        for (int a = 0; a < array.papersArray.length; a++) {
            if (year == array.papersArray[a].publicationDate.year) {
                for (int b = 0; b < array.papersArray.length; b++) {
                    if (month == array.papersArray[b].publicationDate.month) {
                        for (int c = 0; c < array.papersArray.length; c++) {
                            if (day == array.papersArray[c].publicationDate.day && a == b && a == c && b == c) {
                                printLine(array, c);
                                state = true;
                            }
                        }
                    }
                }
            }
        }
        if (!state) {
            System.out.println("There was no paper found with this publication date.");
        }
    }
    public static void referenceSearch(PaperArray array, String referenceName) {
        boolean matches = false;
        int idIndex = -1;
        for (int i = 0; i < array.papersArray.length; i++) {
            if (array.papersArray[i].title.contains(referenceName)) {
                idIndex = i;
                matches = true;
                System.out.printf("%s is included in:\n", referenceName);
            }
        }
        if (matches) {
            for (int a = 0; a < array.papersArray.length; a++) {
                for (ListNode cur = array.papersArray[a].refs; cur != null; cur = cur.next) {
                    for (int b = 0; b < array.papersArray.length; b++) {
                        if (cur.data == idIndex && cur.data == array.papersArray[b].id) {
                            printLine(array, a);
                        }
                    }
                }
            }
        } else {
            System.out.println("There was no reference found with this title.");
        }
    }
    public static void pagesSearch(PaperArray array, int amountOfPages) {
        boolean state = false;
        for (int b = 0; b < array.papersArray.length; b++) {
            if (array.papersArray[b].pages == amountOfPages) {
                state = true;
                printLine(array, b);
            }
        }
        if (!state) {
            System.out.println("There was no paper found with amount of pages.");
        }
    }
    public static void weekdaySearch(PaperArray array, String weekday) {
        boolean matches = false;
        for (int i = 0; i < array.papersArray.length; i++) {
            if (array.papersArray[i].weekday.compareToIgnoreCase(weekday) == 0) {
                matches = true;
                printLine(array, i);
            }
        }
        if (!matches) {
            System.out.println("There was no paper published on this weekday.");
        }
    }

    public static Paper[] addReference(PaperArray array, int paperToReferenceTo, int[] toReferenceToArray) {
        int index = paperToReferenceTo;
        ListNode head = null;
        for (int i = 0; i < toReferenceToArray.length; i++) {
            head = addFront(head, array.papersArray[toReferenceToArray[i]].id);
        }
        array.papersArray[index].refs = head;
        return array.papersArray;
    }
    public static ListNode addFront(ListNode head, int id) {
        ListNode newNode = new ListNode();
        newNode.data = id;
        newNode.next = head;
        return newNode;
    }
    public static void printReferences(PaperArray array) {
        System.out.println("---");
        for (int i = 0; i < array.papersArray.length; i++) {
            System.out.printf("[%2d]: %-20s %-15s\n", i + 1, array.papersArray[i].author, array.papersArray[i].title);
        }
        System.out.printf("--- %d element(s).\n", array.papersArray.length);
    }
    public static void printWhichReferencesCanStillLinkToThis(PaperArray array, int index) {
        ListNode head = array.papersArray[index].refs;
        System.out.println("---");
        int idIndex = array.papersArray[index].id;
        int[] refs = new int[countNumberOfReferences(head)];
        refs = toArray(head, refs);
        int[] ids = new int[array.papersArray.length];
        for (int i = 0; i < array.papersArray.length; i++) {
            ids[i] = array.papersArray[i].id;
        }
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == idIndex) {
                ids[i] = -1;
            }
        }
        for (int a = 0; a < refs.length; a++) {
            for (int b = 0; b < ids.length; b++) {
                if (refs[a] == ids[b]) {
                    ids[b] = -1;
                }
            }
        }
        for (int c = 0; c < ids.length; c++) {
            if (ids[c] != -1) {
                System.out.printf("[%2d]: ", c + 1);
                for (int d = 0; d < array.papersArray.length; d++) {
                    if (array.papersArray[d].id ==  ids[c]) {
                        System.out.printf("%-20s %-15s\n", array.papersArray[d].author, array.papersArray[d].title);
                    }
                }
            }
        }
        int amountOfIds = 0;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] > -1) {
                amountOfIds += 1;
            }
        }
        System.out.printf("--- %2d element(s)\n", amountOfIds);
    }
    public static boolean listContains(ListNode head, int number) {
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.data == number) {
                return true;
            }
        }
        return false;
    }
    public static int howManyCanBeReferenced(ListNode head, PaperArray array) {
        int amountOfRefs = countNumberOfReferences(head);
        return (array.papersArray.length - 1)   - amountOfRefs;
    }
    public static boolean canThisOneBeReferencedTo(ListNode head, PaperArray array) {
        if (countNumberOfReferences(head) == array.papersArray.length - 1) {
            return false;
        }
        return true;
    }

    public static void statisticalAnalysis(PaperArray array) {
        System.out.printf("Number of publications: %d\n", array.papersArray.length);
        int amountOfPages = 0;
        for (int i = 0; i < array.papersArray.length; i++) {
            amountOfPages += array.papersArray[i].pages;
        }
        System.out.printf("Average number of pages: %d\n", amountOfPages / array.papersArray.length);
        int amountOfReferences = 0;
        for (int i = 0; i < array.papersArray.length; i++) {
            amountOfReferences += countNumberOfReferences(array.papersArray[i].refs);
        }
        System.out.printf("Average amount of references: %d\n", amountOfReferences / array.papersArray.length);
        int maxRefs = 0;
        int a = 0;
        for (int i = 0; i < array.papersArray.length; i++) {
            if (countNumberOfReferences(array.papersArray[i].refs) > maxRefs) {
                maxRefs = countNumberOfReferences(array.papersArray[i].refs);
                a = i;
            }
        }
        System.out.printf("Maximum amount of references: %d   %s   %s\n", maxRefs, array.papersArray[a].author, array.papersArray[a].title);
        int highestId = 0;
        for (int f = 0; f < array.papersArray.length; f++) {
            if (array.papersArray[f].id > highestId) {
                highestId = array.papersArray[f].id;
            }
        }
        int[] refs = new int[highestId];
        for (int c = 0; c < refs.length; c++) {
            refs[c] = 0;
        }
        for (int i = 0; i < array.papersArray.length; i++) {
            for (ListNode cur = array.papersArray[i].refs; cur != null; cur = cur.next) {
                for (int z = 0; z < refs.length; z++) {
                    if (cur.data == z) {
                        refs[z]++;
                    }
                }
            }
        }
        int mostReferenced = -1;
        int mostRefIndex = 0;
        for (int d = 0; d < refs.length; d++) {
            if (refs[d] > mostReferenced) {
                mostReferenced = refs[d];
                mostRefIndex = d;
            }
        }
        System.out.print("Most referenced paper: ");
        printShort(array, mostReferenced);
        printRefs(array);
    }
    public static void printRefs(PaperArray array){
        System.out.println("digraph Citations {");
        for (int i = 0; i < array.papersArray.length; i++) {
            if (countNumberOfReferences(array.papersArray[i].refs) != 0) {
                for (ListNode cur = array.papersArray[i].refs; cur != null;  cur = cur.next) {
                    for (int a = 0; a < array.papersArray.length; a++) {
                        if (array.papersArray[a].id == cur.data) {
                            System.out.printf("     %s%d ->", array.papersArray[i].author, array.papersArray[i].publicationDate.year);
                            System.out.printf(" %s%d\n", array.papersArray[a].author, array.papersArray[a].publicationDate.year);
                        }
                    }
                }
            }
        }
        System.out.println("}");
    }

    public static void sortAuthor(PaperArray array) {
        quickSortAuthor(array, 0, array.papersArray.length - 1);
    }
    public static void quickSortAuthor(PaperArray array, int left, int right) {
        int i;
        if (right <= left) {
            return;
        }
        i = partitionAuthor(array, left, right);
        quickSortAuthor(array, left, i - 1);
        quickSortAuthor(array, i + 1, right);
    }
    public static int partitionAuthor(PaperArray array, int left, int right) {
        if (left == right) return left;
        String pivot = array.papersArray[right].author;
        int i = left - 1, j = right;
        while (true) {
            while (array.papersArray[++i].author.compareTo(pivot) < 0) {

            }
            while (pivot.compareTo(array.papersArray[--j].author) < 0) {
                if (j == left) {
                    break;
                }
            }
            if (i >= j) {
                break;
            }
            swap(array, i, j);
        }
        swap(array, i, right);

        return i;
    }

    public static void swap(PaperArray arr, int i, int j) {
        Paper tmp = arr.papersArray[i];
        arr.papersArray[i] = arr.papersArray[j];
        arr.papersArray[j] = tmp;
    }

    public static void sortTitle(PaperArray array) {
        quickSortTitle(array, 0, array.papersArray.length - 1);
    }
    public static void quickSortTitle(PaperArray array, int left, int right) {
        int i;
        if (right <= left) {
            return;
        }
        i = partitionTitle(array, left, right);
        quickSortTitle(array, left, i - 1);
        quickSortTitle(array, i + 1, right);
    }
    public static int partitionTitle(PaperArray array, int left, int right) {
        if (left == right) return left;
        String pivot = array.papersArray[right].title;
        int i = left - 1, j = right;
        while (true) {
            while (array.papersArray[++i].title.compareToIgnoreCase(pivot) < 0) {

            }
            while (pivot.compareToIgnoreCase(array.papersArray[--j].title) < 0) {
                if (j == left) {
                    break;
                }
            }
            if (i >= j) {
                break;
            }
            swap(array, i, j);
        }
        swap(array, i, right);
        return i;
    }

    public static void sortDate(PaperArray array) {
        quickSortDate(array, 0, array.papersArray.length - 1);
    }
    public static void quickSortDate(PaperArray array, int left, int right) {
        int i;
        if (right <= left) {
            return;
        }
        i = partitionDate(array, left, right);
        quickSortDate(array, left, i - 1);
        quickSortDate(array, i + 1, right);
    }
    public static int partitionDate(PaperArray array, int left, int right) {
        if (left == right) return left;
        int pivot = array.papersArray[right].publicationDate.year * 2000 + array.papersArray[right].publicationDate.month * 100 + array.papersArray[right].publicationDate.day;
        int i = left - 1, j = right;
        while (true) {
            while (array.papersArray[++i].publicationDate.year * 2000 + array.papersArray[i].publicationDate.month * 100 + array.papersArray[i].publicationDate.day < pivot) {

            }
            while (pivot < array.papersArray[--j].publicationDate.year * 2000 + array.papersArray[j].publicationDate.month * 100 + array.papersArray[j].publicationDate.day) {
                if (j == left) {
                    break;
                }
            }
            if (i >= j) {
                break;
            }
            swap(array, i, j);
        }
        swap(array, i, right);
        return i;
    }

    public static int[] insertionSortList(String[] names, int[] ids) {
        for (int i = 1; i < names.length; i++) {
            int j = i;
            String tmp = names[i];
            int tmp2 = ids[i];
            while (j > 0 && tmp.compareTo(names[j - 1]) < 0) {
                names[j] = names[j - 1];
                ids[j] = ids[j - 1];
                j--;
            }
            names[j] = tmp;
            ids[j] = tmp2;
        }
        return ids;
    }
    public static ListNode sortList(ListNode head, PaperArray array) {
        int[] listArr = new int[countNumberOfReferences(head)];
        ListNode sorted = null;
        if (countNumberOfReferences(head) <= 1) {
            return head;
        } else {
            listArr = toArray(head, listArr);
            String[] listArrayAuthor = new String[listArr.length];
            for (int a = 0; a < listArr.length; a++) {
                for (int b = 0; b < array.papersArray.length; b++) {
                    if (listArr[a] == array.papersArray[b].id) {
                        listArrayAuthor[a] = array.papersArray[b].author;
                    }
                }
            }
            int[] listArr2 = new int[countNumberOfReferences(head)];
            for (int i = 0; i < listArr.length; i++) {
                listArr2[i] = listArr[i];
            }
            listArr2 = insertionSortList(listArrayAuthor, listArr2);
            sorted = backToListSort(sorted, listArr2);
        }
        return sorted;
    }
    public static int[] toArray(ListNode head, int[] arr) {
        int i = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            arr[i++] = cur.data;
        }
        return arr;
    }
    public static ListNode backToListSort(ListNode head, int[] arr) {
        ListNode backList = null;
        for (int i = arr.length - 1; i > -1; i--) {
            backList = addFront(backList, arr[i]);
        }
        return backList;
    }
}

class Paper {
    String author;
    String title;
    Date publicationDate;
    String weekday;
    int pages;
    ListNode refs;
    int id;
}

class Date {
    int day;
    int month;
    int year;
}

class PaperArray {
    Paper[] papersArray = new Paper[0];
}

class ListNode {
    int data;
    ListNode next;
}


