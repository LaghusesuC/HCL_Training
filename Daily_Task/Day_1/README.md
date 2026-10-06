# 📝 Day 1 Notes: Java Platform & CLI Fundamentals

A quick revision guide covering JDK CLI tools, JVM memory properties, bytecode inspection, and class loading.

---

## ⚡ Quick Cheat Sheet

| Task | Command | Purpose |
| :--- | :--- | :--- |
| **Check Version** | `java -version` / `javac -version` | Verify JDK installation & version |
| **Compile** | `javac PlatformInfo.java` | Compiles source (`.java`) to bytecode (`.class`) |
| **Run** | `java PlatformInfo` | JVM executes bytecode via `main` method |
| **Inspect Bytecode** | `javap -c PlatformInfo` | Disassembles `.class` file into human-readable opcodes |
| **Trace Class Loading** | `java -verbose:class PlatformInfo` | Displays every class loaded by JVM at runtime |

---

## 💻 Source Code: `PlatformInfo.java`

```java
public class PlatformInfo {
    public static void main(String[] args) {
        // System Properties
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");

        // Runtime Resources
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        long maxMemory = runtime.maxMemory();   // in bytes
        long freeMemory = runtime.freeMemory(); // in bytes

        // Output
        System.out.println("Java Version        : " + javaVersion);
        System.out.println("Operating System    : " + osName);
        System.out.println("Available Processors: " + processors);
        System.out.printf("Max Heap Memory     : %d MB%n", maxMemory / (1024 * 1024));
        System.out.printf("Free Heap Memory    : %d MB%n", freeMemory / (1024 * 1024));
    }
}
```

### Sample Output
```text
Java Version        : 17.0.20.1 (or 21.x.x)
Operating System    : Windows 11
Available Processors: 8
Max Heap Memory     : 1492 MB
Free Heap Memory    : 92 MB
```

---

## 🧠 Key Concepts for Revision

### 1. `System` vs `Runtime`
- **`System.getProperty(key)`**: Accesses environment and platform properties configured by JVM (e.g., `"java.version"`, `"os.name"`, `"user.home"`).
- **`Runtime.getRuntime()`**: A **Singleton** representing the current JVM process. Gives access to hardware resources:
  - `availableProcessors()`: Logical CPU cores available to JVM.
  - `maxMemory()`: Maximum heap memory JVM will attempt to use (`-Xmx`).
  - `totalMemory()`: Total memory currently allocated to JVM.
  - `freeMemory()`: Free memory available within the allocated heap.

---

### 2. Bytecode Disassembly (`javap -c`)
Bytecode is intermediate, platform-independent instruction code executed by the JVM.

**Key Opcodes to Remember:**
- `invokestatic`: Calls static methods (e.g., `System.getProperty()`, `Runtime.getRuntime()`).
- `invokevirtual`: Calls standard instance methods via dynamic dispatch (e.g., `runtime.availableProcessors()`, `out.println()`).
- `invokespecial`: Calls private methods, `super` methods, or constructors (`<init>`).
- `invokedynamic`: Bootstrap-based call introduced for lambdas and efficient string concatenation (`makeConcatWithConstants`).
- `lload` / `lstore` / `ldiv`: Stack operations for 64-bit `long` integers.

---

### 3. Class Loading Lifecycle (`-verbose:class`)
- The JVM doesn't load all classes upfront; classes are loaded **lazily on-demand**.
- When running `java -verbose:class PlatformInfo`, notice:
  1. **Bootstrap classes** load first (`java.lang.Object`, `String`, `System`, `ClassLoader`).
  2. Classes are loaded from `shared objects file` (CDS cache / runtime image).
  3. Finally, your application class (`PlatformInfo`) is loaded and its `main()` method is invoked.

---

## 🎯 Quick Self-Check / Interview Questions

1. **Why do we compile with `javac` and run with `java`?**  
   `javac` translates source `.java` into `.class` bytecode; `java` starts the JVM and runs the bytecode.
2. **What design pattern is `Runtime.getRuntime()`?**  
   Singleton pattern (only one runtime environment per JVM instance).
3. **What is `javap`?**  
   The Java class file disassembler tool bundled in the JDK used to inspect bytecode and compiler optimizations.
