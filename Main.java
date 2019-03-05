package net.ukr.andy777;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.*;

/*
 Lesson07.3
 Реализуйте процесс многопоточного поиска файла в файловой системе. 
 Т.е. вы вводите название файла и в какой части файловой системы его искать. 
 Программа должна вывести на экран все адреса файлов с таким названием.
 */

public class Main {
	static final int THREADS_COUNT = 2; // кількість потоків виконання

	public static void main(String[] args) {
		String fileSearch = "Main.java"; // файл, що шукаємо
		File folderSearch = new File("d://JavaProjects"); // папка де шукаємо файл

		// 1.класичний пошук
		classicSearch(fileSearch, folderSearch);

		// 2.пошук з використанням пулу потоків
		fixedThreadPoolSearch(fileSearch, folderSearch);

		// 3.пошук з використанням пулу потоків
		сompletionServiceSearch(fileSearch, folderSearch);

	}

	// 1.класичний пошук
	public static void classicSearch(String fileSearch, File folderSearch) {
		long tstart = System.currentTimeMillis();
		System.out.println("CLASSIC recursive search");
		String result = SearchFile.searchFile(fileSearch, folderSearch); // результат
		printRes(result);
		System.out.println(" -- " + (System.currentTimeMillis() - tstart) + " ms"
				+ System.getProperty("line.separator"));
	}

	// 2.пошук з використанням пулу потоків
	public static void fixedThreadPoolSearch(String fileSearch, File folderSearch) {
		String result = ""; // результат
		long tstart = System.currentTimeMillis();
		System.out.println("Fixed Thread Pool search (одноразове додавання результатів = по завершенні всіх потоків)");
		ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT); // ініціалізація пулу потоків
		List<Future<String>> results = new ArrayList<Future<String>>(); // ініціалізазція результатів виконання потоків

		try { // для кожного файлу/папки в папці-пошуку запускаємо поток в пулі
			File[] fileArray = folderSearch.listFiles();
			for (File file : fileArray) {
				Future<String> future = service.submit(new SearchFile(fileSearch, file)); // запуск потоку
				results.add(future); // приєднання результату виконання потоку до листа результатів
			}
			for (Future<String> future : results) {
				try { // об'єднання отриманих результатів String в один елемент
					result += future.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			printRes(result);
		} finally {
			service.shutdown(); // закриття пулу потоків
		}
		System.out.println(" -- " + (System.currentTimeMillis() - tstart) + " ms"
				+ System.getProperty("line.separator"));
	}

	// 3.пошук з використанням пулу потоків
	public static void сompletionServiceSearch(String fileSearch, File folderSearch) {
		String result = ""; // результат
		long tstart = System.currentTimeMillis();
		System.out
				.println("Fixed Thread Pool + Completion Service search (постійне додавання результатів = по завершенні кожного потоку)");
		ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT); // ініціалізація пулу потоків
		CompletionService<String> сompletionService = new ExecutorCompletionService<String>(service);

		try { // для кожного файлу/папки в папці-пошуку запускаємо поток в пулі
			File[] fileArray = folderSearch.listFiles();
			for (File file : fileArray) {
				сompletionService.submit(new SearchFile(fileSearch, file)); // запуск потоку
			}
			for (File file : fileArray) {
				try { // об'єднання отриманих результатів String в один елемент
					Future<String> future = сompletionService.take(); // запуск потоку
					result += future.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			printRes(result);
		} finally {
			service.shutdown(); // закриття пулу потоків
		}
		System.out.println(" -- " + (System.currentTimeMillis() - tstart) + " ms"
				+ System.getProperty("line.separator"));
	}

	// друк String-результату поелементними рядками
	public static void printRes(String result) {
		// перетворення результуючого значення String на масив (розділювач - розрив рядка)
		for (String string : result.split(System.getProperty("line.separator")))
			System.out.println(string); // друк кожного елементу масиву
	}

}
