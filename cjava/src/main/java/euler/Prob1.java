package euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.utils.Prime;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.math.BigIntegerMath;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;

public class Prob1 {

    final static Logger log = LoggerFactory.getLogger(Prob1.class);
    
    public static void main(String args[]) throws Exception {
        ///////////////////////////////////////
        //1
        int sum = 0;
        for(int i = 1; i < 1000; ++i ) {
            if (i%3 ==0 || i % 5 == 0)
                sum +=i;
        }
        log.info("Prob 1 Sum {}", sum);
        
        ///////////////////////////////////////
        //2
        
        sum = 2;
        int fibBack2 = 1;
        int fibBack1 = 2;
        
        while(fibBack1 <= 4000000) {
            int fib = fibBack1 + fibBack2;
            fibBack2 = fibBack1;
            fibBack1 = fib;
            if (fib % 2 == 0) {
                sum += fib;
            }
        }
        
        log.info("Prob 2 sum {}", sum);
        
        //////////////////////////////////////
        //3
        long num = 600851475143L;
        
        int sqRootNum = Ints.checkedCast( LongMath.sqrt(num, RoundingMode.UP) );
        
        List<Integer> primes = Prime.generatePrimes(sqRootNum);
        
        for(int i = primes.size() - 1; i >= 0; --i) {
            if (num % primes.get(i) == 0) {
                log.info("Prob 3 Prime is {}", primes.get(i));
                break;
            }
        }
        ///////////////////////////////////////
        //4
        
        int largest = 0;
        
        for(int i = 100; i <= 999; ++i) {
            for(int j = 100; j <= 999; ++j) {
                sum = i * j;
                
                int toRev = sum;
                int rev = 0;
                
                while(toRev != 0) {
                    rev *= 10;
                    rev += toRev % 10;
                    toRev /= 10;
                }
                if (rev == sum && largest < rev) {
                    largest = sum;
                }
            }
        }
        log.info("Prob 4.  Largest palin {}", largest);
        
        primes = Prime.generatePrimes(20);
        
        Multiset<Integer> primeFactorizationOfN = HashMultiset.create();
        
        for(int prime : primes) {
            primeFactorizationOfN.add(prime);
        }
        
        for(int i = 2; i <= 20; ++i) {
            Multiset<Integer> primeFactorizationOfDivisor = HashMultiset.create();
            //get prime factorization
            int divisor = i;
            
            while(divisor > 1) {
                for(int prime : primes) {
                    if (divisor % prime == 0) {
                        divisor /= prime;
                        primeFactorizationOfDivisor.add(prime);
                    }
                }
            }
            
            for(Integer pf : primeFactorizationOfDivisor) {
                if (primeFactorizationOfDivisor.count(pf) > primeFactorizationOfN.count(pf)) {
                    primeFactorizationOfN.setCount(pf, primeFactorizationOfDivisor.count(pf));
                }
            }
            
        }
        
        long n = 1;
        
        for(Integer pf : primeFactorizationOfN) {
            n *= pf;
        }
        
        log.info("Prob 5.  Divisible by 1-20 : {}", n);
        
//        for(long i = 1; i < 1000000000; ++i) {
//            boolean found = true;
//            
//            for(int j = 2; j <= 20; ++j) {
//                if (i % j != 0) {
//                    found = false;
//                    break;
//                }                    
//            }
//            if (found) {
//            log.info("Prob 5.  Divisible by 1-20 : {}", i);
//            break;
//            }
//        }
        
        
        n = 100;
        
        long sumSq = n * (n+1) * (2*n+1) /  6;
        long sqOfSum = n * (n+1) / 2;
        sqOfSum *= sqOfSum;
        
        log.info("Prob 6.   (1+2+...)^2 - (1^2 + 2^2 + ..)  {}", sqOfSum - sumSq);
        
        
        primes = Prime.generatePrimes(120000);
        log.info("Prob 7. primes 1st {} 10001 - {}", primes.get(0), primes.get(10000));

        String s = 
        "73167176531330624919225119674426574742355349194934" +
        "96983520312774506326239578318016984801869478851843" +
        "85861560789112949495459501737958331952853208805511" +
        "12540698747158523863050715693290963295227443043557" +
        "66896648950445244523161731856403098711121722383113" +
        "62229893423380308135336276614282806444486645238749" +
        "30358907296290491560440772390713810515859307960866" +
        "70172427121883998797908792274921901699720888093776" +
        "65727333001053367881220235421809751254540594752243" +
        "52584907711670556013604839586446706324415722155397" +
        "53697817977846174064955149290862569321978468622482" +
        "83972241375657056057490261407972968652414535100474" +
        "82166370484403199890008895243450658541227588666881" +
        "16427171479924442928230863465674813919123162824586" +
        "17866458359124566529476545682848912883142607690042" +
        "24219022671055626321111109370544217506941658960408" +
        "07198403850962455444362981230987879927244284909188" +
        "84580156166097919133875499200524063689912560717606" +
        "05886116467109405077541002256983155200055935729725" +
        "71636269561882670428252483600823257530420752963450";
        
        int max = 0;
        for(int i = 0; i < s.length() - 5; ++i) {
            String sub = s.substring(i, i+5);
            int prod = 1;
            for(int j=0; j<5; ++j) {
                prod *= Character.digit( sub.charAt(j), 10 );
            }
            max = Math.max(max, prod);
        }
        
        log.info("Prob 8 {}", max);
        
        outer:
        for(int i = 1; i <= 1000; ++i) {
            for(int j = i + 1; j <= 1000; ++j) {
                int k = 1000 - i - j;
                if (k <= j)
                    continue;
                
                if (k*k == i*i + j*j) {
                    log.info("Prob 9 {} {} {} --- prod {}", i,j,k,i*j*k);
                    break outer;
                }
            
            }
        }
        
        primes = Prime.generatePrimes(2000000);
        
        long sumL = 0;
        for(int p : primes) {
            sumL += p;
        }
        
        log.info("Prob 10 : {}", sumL);
        
        int[][] gridNum = new int[][] { 
              { 8, 02, 22, 97, 38, 15, 00, 40, 00, 75, 04, 05, 07, 78, 52, 12, 50, 77, 91, 8 },     
              { 49, 49, 99, 40, 17, 81, 18, 57, 60, 87, 17, 40, 98, 43, 69, 48, 04, 56, 62, 00 },
              { 81, 49, 31, 73, 55, 79, 14, 29, 93, 71, 40, 67, 53, 88, 30, 03, 49, 13, 36, 65 },
              { 52, 70, 95, 23, 04, 60, 11, 42, 69, 24, 68, 56, 01, 32, 56, 71, 37, 02, 36, 91 },
              { 22, 31, 16, 71, 51, 67, 63, 89, 41, 92, 36, 54, 22, 40, 40, 28, 66, 33, 13, 80 },
              { 24, 47, 32, 60, 99, 03, 45, 02, 44, 75, 33, 53, 78, 36, 84, 20, 35, 17, 12, 50 },
              { 32, 98, 81, 28, 64, 23, 67, 10, 26, 38, 40, 67, 59, 54, 70, 66, 18, 38, 64, 70 },
              { 67, 26, 20, 68, 02, 62, 12, 20, 95, 63, 94, 39, 63, 8, 40, 91, 66, 49, 94, 21 },
              { 24, 55, 58, 05, 66, 73, 99, 26, 97, 17, 78, 78, 96, 83, 14, 88, 34, 89, 63, 72 },
              { 21, 36, 23, 9, 75, 00, 76, 44, 20, 45, 35, 14, 00, 61, 33, 97, 34, 31, 33, 95 },
              { 78, 17, 53, 28, 22, 75, 31, 67, 15, 94, 03, 80, 04, 62, 16, 14, 9, 53, 56, 92 },
              { 16, 39, 05, 42, 96, 35, 31, 47, 55, 58, 88, 24, 00, 17, 54, 24, 36, 29, 85, 57 },
              { 86, 56, 00, 48, 35, 71, 89, 07, 05, 44, 44, 37, 44, 60, 21, 58, 51, 54, 17, 58 },
              { 19, 80, 81, 68, 05, 94, 47, 69, 28, 73, 92, 13, 86, 52, 17, 77, 04, 89, 55, 40 },
              { 04, 52, 8, 83, 97, 35, 99, 16, 07, 97, 57, 32, 16, 26, 26, 79, 33, 27, 98, 66 },
              { 88, 36, 68, 87, 57, 62, 20, 72, 03, 46, 33, 67, 46, 55, 12, 32, 63, 93, 53, 69 },
              { 04, 42, 16, 73, 38, 25, 39, 11, 24, 94, 72, 18, 8, 46, 29, 32, 40, 62, 76, 36 },
              { 20, 69, 36, 41, 72, 30, 23, 88, 34, 62, 99, 69, 82, 67, 59, 85, 74, 04, 36, 16 },
              { 20, 73, 35, 29, 78, 31, 90, 01, 74, 31, 49, 71, 48, 86, 81, 16, 23, 57, 05, 54 },
              { 01, 70, 54, 71, 83, 51, 54, 69, 16, 92, 33, 48, 61, 43, 52, 01, 89, 19, 67, 48 }
        };
        
        max = 0;
        for(int r = 0; r < 20; ++r) {
            for(int c = 0; c < 20; ++c) {
                //Vertical
                if (r <= 16) {
                    int prod = 1;
                    for(int deltaR = 0; deltaR < 4; ++deltaR) {
                        prod *= gridNum[r+deltaR][c];
                    }
                    max = Math.max(max, prod);
                }
                
                //Horizontal
                if (c <= 16) {
                    int prod = 1;
                    for(int deltaC = 0; deltaC < 4; ++deltaC) {
                        prod *= gridNum[r][c+deltaC];
                    }
                    max = Math.max(max, prod);
                }
                
                //Diag
                if (r <= 16 && c <= 16) {
                    int prod = 1;
                    for(int delta = 0; delta < 4; ++delta) {
                        prod *= gridNum[r+delta][c+delta];
                    }
                    max = Math.max(max, prod);
                }
                
                //Other diag
                if (r <= 16 && c >= 3) {
                    int prod = 1;
                    for(int delta = 0; delta < 4; ++delta) {
                        prod *= gridNum[r+delta][c-delta];
                    }
                    max = Math.max(max, prod);
                }
            }
        }
        
        log.info("Prob 11.  Sum {}", max);
        
        /*
        
        for(n = 1; n < 10000000; ++n) {
            long triangle = n * (n+1) / 2;
            
            Set<Long> factors = Sets.newHashSet();
            
            long upperLimit = LongMath.sqrt(triangle,RoundingMode.UP);
            
            for(long factor = 1; factor <= upperLimit; ++factor) {
                if (triangle % factor == 0) {
                    factors.add(factor);
                    factors.add(triangle / factor);
                }
            }
            
            if (factors.size() > 500 ) {
                log.info("Prob 12.  Triangle num {}", triangle);
                break;
            }
            
        }*/
    
        
        ///
        Scanner scanner = new Scanner(Prob1.class.getResourceAsStream("prob12.txt"));
        
        BigInteger sumBI = BigInteger.ZERO;
        
        for(int i = 0; i < 100; ++i) {
            BigInteger next = scanner.nextBigInteger();
            sumBI = sumBI.add(next);
        }
        
        log.info("Prob 13. Sum {}", sumBI.toString().substring(0, 10));
        scanner.close();
        
        /*
        int maxCount = 0;
        
        for(long start = 1; start < 1000000; ++start ) {
            int count = 0;
            long seq = start;
            while(seq != 1) {
                seq = seq % 2 == 0 ? seq / 2 : 3 * seq + 1;
                ++count;
            }
            if (count > maxCount) {
                maxCount = count;
               // log.info("Prob 14.  Seq length {}  start {}", count, start);
            }
        }*/
        
        s = BigInteger.valueOf(2).pow(1000).toString();
        
        sum = 0;
        for(int i = 0; i < s.length(); ++i) {
            sum += Character.digit(s.charAt(i),10);
        }
        
        log.info("Prob 16  sum {}", sum);

        //1 to 10
        int wordCounts[] = {3, 3, 5, 4, 4, 3, 5, 5, 4, 3, 6, "twelve".length(), "thirteen".length(),
                8, 7, 7, 9, "eighteen".length(), 8};
        int tenCounts[] = {"twenty".length(), "thirty".length(), "forty".length(), "fifty".length(),
                "sixty".length(), "seventy".length(), "eighty".length(), "ninety".length() };
        int hundred = "hundred".length();
        int thousand = "thousand".length();
        
        sum = 0;
        for(int i = 1; i <= 1000; ++i) {            
            if (i >= 1000) {
                sum += wordCounts[0] + thousand;
                continue;
            }
            
            int rest = i;
            if (i >= 100) {
                int hundredsDigit = wordCounts[i / 100 - 1] + hundred;
                sum += hundredsDigit;
                rest = i % 100;
            }
            
            if (rest == 0)
                continue;
            
            if (i >= 100) {
                sum += 3; //and
            }
            
            if (rest < 20) {
                sum += wordCounts[rest-1];
                continue;
            }
            
            int tensDigit = rest / 10;
            sum += tenCounts[tensDigit - 2];
            
            int onesDigit = rest % 10;
            
            if (onesDigit > 0) {
                sum += wordCounts[onesDigit-1];
            }
            
            
        }
        
        log.info("Prob 17 : sum {}", sum);
        
        //scanner = new Scanner(Prob1.class.getResourceAsStream("prob18.txt"));
        scanner = new Scanner(Prob1.class.getResourceAsStream("prob67.txt"));
        
        int nodeNum = 1;
        
        List<Integer> maximumPath  = Lists.newArrayList();
        maximumPath.add(scanner.nextInt());
        final int maxRow = 100;
        int globalMaxVal = 0;
        for(int r = 2; r <= maxRow; ++r) {
            for(int c = 1; c <= r; ++c) {
                ++nodeNum;
                int val = scanner.nextInt();
                
                int maxValue = 0;
                //subtract length of previous row
                if (c > 1) {
                    maxValue = Math.max(maxValue, val+maximumPath.get( nodeNum-r-1));
                }
                if (c < r) {
                    maxValue = Math.max(maxValue, val+maximumPath.get( nodeNum-r));
                }
                maximumPath.add(maxValue);
                globalMaxVal = Math.max(globalMaxVal,maxValue);
            }
        }
        
        log.info("Prob 18.  max sum {}", globalMaxVal);
        
        sum = 0;
        for (int y = 1901; y <= 2000; ++y) {
            for(int m = 1; m <= 12; ++m) {
                LocalDate dt = new LocalDate(y, m, 1);
                if (dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                    sum++;
                }
                //log.info("Date time {}", dt.getDayOfWeek());
            }
        }
        
        log.info("Prob 19 {}", sum);
        
        
        String hugeFactorial = BigIntegerMath.factorial(100).toString();
        
        sum = 0;
        
        for(int i = 0; i < hugeFactorial.length(); ++i) {
            sum += Character.digit(hugeFactorial.charAt(i),10);
        }
        
        log.info("Prob 20 {}", sum);
        
        int amiable = 0;
        int[] sums = new int[10000];
        for(int i = 1; i < 10000; ++i) {
            
            int upperLimit = IntMath.sqrt(i,RoundingMode.UP);
            
            sum = 0;
            for(int factor = 1; factor <= upperLimit; ++factor) {
                if (i % factor == 0) {
                    sum += factor;
                    sum += i / factor;
                }
            }
            
            //Only proper factors
            sum -= i;
            
            sums[i-1] = sum;
            if (sum < i && sum > 0 && sums[sum-1] == i ) {
                log.info("Found amiable pair n {} : {} and {} : {}", i, sum, sum, sums[sum-1]);
                amiable+=i;
                amiable += sum;
            }
            
        }
        
        log.info("prob 21 : {}", amiable);
    }

}