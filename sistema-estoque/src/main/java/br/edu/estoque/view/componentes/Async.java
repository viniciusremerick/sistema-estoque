package br.edu.estoque.view.componentes;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/**
 * Executa consultas ao banco fora da thread de interface (EDT) para a tela não travar.
 * Os callbacks de sucesso e erro sempre rodam na EDT.
 */
public final class Async {

    private Async() {
    }

    public static <T> void executar(Callable<T> tarefa, Consumer<T> aoConcluir, Consumer<Exception> aoFalhar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarefa.call();
            }

            @Override
            protected void done() {
                try {
                    aoConcluir.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    aoFalhar.accept(causa instanceof Exception ex ? ex : new Exception(causa));
                }
            }
        }.execute();
    }
}
