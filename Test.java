public class Test {
    public static void main(String[] args) {
        System.out.println(checkAns("2 / 1 * 3 *4"));
    }

    public static boolean checkAns(String answer){
        int sum = 0;
        answer = answer.trim();
        answer = clearWSpace(answer);
        int[] num = getNumber(answer);
        for(int i = 1; i < answer.length(); i = i+2){
            char c = answer.charAt(i);
            if (c < '0' || c > '9') {
                switch (c) {
                    case '+':
                        if(i == 1){
                            sum = num[0] + num[1];
                        }
                        if(i == 3){
                            sum = sum + num[2];
                        }
                        if (i == 5) {
                            sum = sum + num[3];
                        }
                        break;
                    case '-':
                        if(i == 1){
                            sum = num[0] - num[1];
                        }
                        if(i == 3){
                            sum = sum - num[2];
                        }
                        if (i == 5) {
                            sum = sum - num[3];
                        }
                        break;
                    case '*':
                        if(i == 1){
                            sum = num[0] * num[1];
                        }
                        if(i == 3){
                            sum = sum * num[2];
                        }
                        if (i == 5) {
                            sum = sum * num[3];
                        }
                        break;
                    case '/':
                        if(i == 1){
                            sum = num[0] / num[1];
                        }
                        if(i == 3){
                            sum = sum / num[2];
                        }
                        if (i == 5) {
                            sum = sum / num[3];
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        
        return sum == 24;
    }

    private static String clearWSpace(String str){
        return str.replaceAll(" ", "");
    }

    private static int[] getNumber(String answer){
        int[] res = new int[4];
        int index = 0;
        for(int i = 0; i < answer.length(); i = i + 2){
            char c = answer.charAt(i);
            int num = c - '0';
            res[index] = num;
            index++;
        }
        return res;
    }
}
