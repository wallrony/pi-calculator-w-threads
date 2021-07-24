import java.util.Arrays;

public class Main {
  private static int EXECUTION_QUANTITY = 5;

  public static void main(String[] args) throws InterruptedException {
    if (args.length != 2) {
      System.out.println(
          "Quantidade de parâmetros incorreta! Você precisa passar a quantidade de termos e a quantidade de Threads a serem utilizadas.");
      System.exit(-1);
    }

    int qtdTerms = 0;
    int qtdThreads = 0;

    try {
      qtdTerms = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println("Argumento errado: necessita-se de um inteiro como número de termos.");

      System.exit(-1);
    }

    try {
      qtdThreads = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.out.println("Argumento errado: necessita-se de um inteiro como número de threads.");

      System.exit(-1);
    }

    double[] executionTimes = new double[EXECUTION_QUANTITY];
    double[] pi = new double[EXECUTION_QUANTITY];

    for (int i = 0; i < EXECUTION_QUANTITY; i++) {
      long timeBeforeExecution = System.nanoTime();

      pi[i] = executeThreadSet(qtdThreads, (int) qtdTerms / qtdThreads);

      double timeAfterExecution = (double) (System.nanoTime() - timeBeforeExecution) / (double) 1000_000;

      executionTimes[i] = timeAfterExecution;
    }

    double defaultDeviation = calculateDefaultDeviation(executionTimes);
    double executionTimeAvg = calculateAverage(executionTimes);

    showResults(pi, executionTimeAvg, defaultDeviation);
  }

  private static double calculateDefaultDeviation(double valuesArr[]) {
    double valueAvg = calculateAverage(valuesArr);

    double valuesSum = 0;
    double defaultDeviation = 0;

    for (int i = 0; i < valuesArr.length; i++) {
      double term = valuesArr[i] - valueAvg;

      valuesSum += Math.pow(term, 2) / (double) valuesArr.length;
    }

    defaultDeviation = Math.sqrt(valuesSum);

    return defaultDeviation;
  }

  private static double calculateAverage(double valuesArr[]) {
    double avg = 0;

    for (int i = 0; i < valuesArr.length; i++) {
      avg += valuesArr[i];
    }

    avg /= (double) valuesArr.length;

    return avg;
  }

  private static double executeThreadSet(int qtdThreads, int threadTermsQtd) throws InterruptedException {
    MyThread[] threadArr = new MyThread[qtdThreads];
    double pi = 0;

    for (int j = 0; j < qtdThreads; j++) {
      threadArr[j] = new MyThread(j * threadTermsQtd, threadTermsQtd);

      threadArr[j].start();
    }

    for (int j = 0; j < qtdThreads; j++) {
      threadArr[j].join();

      pi += threadArr[j].pi;
    }

    return pi;
  }

  private static void showResults(double[] pi, double executionTimeAvg, double defaultDeviation) {
    System.out.printf("Execution Quantity: %d\n", EXECUTION_QUANTITY);
    System.out.printf("Calculated PI Value: %s\n", Arrays.toString(pi));
    System.out.printf("Average time: %.4fms\n", executionTimeAvg);
    System.out.printf("Default Deviation: %.4fms\n", defaultDeviation);
    System.out.printf("Variation Coefficient: %.2f%%\n", (defaultDeviation / executionTimeAvg) * 100);
  }

  private static class MyThread extends Thread {
    double pi;
    int lastIndex;
    int qtdTerms;

    MyThread(int lastIndex, int qtdTerms) {
      this.lastIndex = lastIndex;
      this.qtdTerms = qtdTerms;
    }

    @Override
    public void run() {
      double sum = 0;

      for (int i = lastIndex; i < qtdTerms; i++) {
        sum += Math.pow(-1, i) / (2 * i + 1);
      }

      sum *= 4;

      this.pi += sum;
    }
  }
}
