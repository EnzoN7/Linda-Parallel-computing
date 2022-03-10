package linda.search.basic;
public enum Code {
    Request, // Request, UUID, String
    Value,   // Value, String
    Result,   // Result, UUID, String, Int
    Searcher, // Result, "done", UUID
    Best, // Best, UUID, String (value), Integer (value)
}
