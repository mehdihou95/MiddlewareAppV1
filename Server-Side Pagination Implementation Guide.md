Server-Side Pagination Implementation Guide
1. Backend Changes
First, let's modify the backend to support pagination, sorting, and filtering:
1.1. Update Repository Methods
For each repository that needs pagination (starting with ClientRepository):
java
// In ClientRepository.java
public interface ClientRepository extends JpaRepository<Client, Long> {
    // These methods are already provided by JpaRepository:
    // Page<Client> findAll(Pageable pageable);
    
    // Add custom methods for filtering if needed
    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Client> findByStatus(ClientStatus status, Pageable pageable);
}
1.2. Update Service Layer
java
// In ClientService.java
public interface ClientService {
    Page<Client> getClients(int page, int size, String sortBy, String direction, String nameFilter, ClientStatus statusFilter);
    // Other methods...
}

// In ClientServiceImpl.java
@Override
public Page<Client> getClients(int page, int size, String sortBy, String direction, 
                              String nameFilter, ClientStatus statusFilter) {
    Sort sort = direction.equalsIgnoreCase("asc") ? 
        Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    
    PageRequest pageRequest = PageRequest.of(page, size, sort);
    
    // Apply filters if provided
    if (nameFilter != null && !nameFilter.isEmpty()) {
        return clientRepository.findByNameContainingIgnoreCase(nameFilter, pageRequest);
    } else if (statusFilter != null) {
        return clientRepository.findByStatus(statusFilter, pageRequest);
    }
    
    // No filters, return all with pagination
    return clientRepository.findAll(pageRequest);
}
1.3. Update Controller
java
// In ClientController.java
@GetMapping("/api/clients")
public ResponseEntity<Page<Client>> getClients(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String nameFilter,
        @RequestParam(required = false) ClientStatus statusFilter) {
    
    Page<Client> clients = clientService.getClients(page, size, sortBy, direction, nameFilter, statusFilter);
    return ResponseEntity.ok(clients);
}
2. Frontend Changes
2.1. Update Client Service
typescript
// In clientService.ts
export const clientService = {
  // Update the getAllClients method to support pagination
  getAllClients: async (
    page: number = 0,
    size: number = 10,
    sortBy: string = 'name',
    direction: string = 'asc',
    nameFilter?: string,
    statusFilter?: string
  ): Promise<PageResponse<Client>> => {
    // Build query parameters
    let params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    params.append('sortBy', sortBy);
    params.append('direction', direction);
    
    if (nameFilter) {
      params.append('nameFilter', nameFilter);
    }
    
    if (statusFilter) {
      params.append('statusFilter', statusFilter);
    }
    
    const response = await axios.get(`/api/clients?${params.toString()}`);
    return response.data;
  },
  // Other methods...
};

// Add a PageResponse interface in types/index.ts
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
2.2. Update ClientManagementPage Component
typescript
// In ClientManagementPage.tsx
const ClientManagementPage: React.FC = () => {
  // State variables
  const [clients, setClients] = useState<Client[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [orderBy, setOrderBy] = useState<keyof Client>('name');
  const [order, setOrder] = useState<'asc' | 'desc'>('asc');
  const [nameFilter, setNameFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | null>(null);
  
  // Load clients with server-side pagination
  const loadClients = async () => {
    try {
      setLoading(true);
      const response = await clientService.getAllClients(
        page,
        rowsPerPage,
        orderBy,
        order,
        nameFilter || undefined,
        statusFilter || undefined
      );
      
      setClients(response.content);
      setTotalCount(response.totalElements);
      setError(null);
    } catch (err) {
      const axiosError = err as any;
      setError(axiosError.response?.data?.message || 'Failed to load clients');
    } finally {
      setLoading(false);
    }
  };
  
  // Effect to reload when pagination, sorting, or filters change
  useEffect(() => {
    loadClients();
  }, [page, rowsPerPage, orderBy, order, nameFilter, statusFilter]);
  
  // Handle page change
  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };
  
  // Handle rows per page change
  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0); // Reset to first page
  };
  
  // Handle sort request
  const handleRequestSort = (property: keyof Client) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };
  
  // Add filter components
  const handleNameFilterChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNameFilter(event.target.value);
    setPage(0); // Reset to first page when filter changes
  };
  
  const handleStatusFilterChange = (event: React.ChangeEvent<{ value: unknown }>) => {
    setStatusFilter(event.target.value as string);
    setPage(0); // Reset to first page when filter changes
  };
  
  // Rest of the component...
  
  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}>
      {/* Header and Add Client button */}
      
      {/* Filters */}
      <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
        <TextField
          label="Filter by name"
          variant="outlined"
          size="small"
          value={nameFilter}
          onChange={handleNameFilterChange}
        />
        <FormControl variant="outlined" size="small" sx={{ minWidth: 150 }}>
          <InputLabel>Status</InputLabel>
          <Select
            value={statusFilter || ''}
            onChange={handleStatusFilterChange}
            label="Status"
          >
            <MenuItem value="">All</MenuItem>
            <MenuItem value="ACTIVE">Active</MenuItem>
            <MenuItem value="INACTIVE">Inactive</MenuItem>
            <MenuItem value="SUSPENDED">Suspended</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      {/* Table with data */}
      
      {/* Pagination component - this stays mostly the same */}
      <TablePagination
        component="div"
        count={totalCount}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[5, 10, 25, 50]}
      />
      
      {/* Rest of the component... */}
    </Box>
  );
};
3. Testing the Implementation
1. Test the backend API with Postman or similar tool:
* Try different page sizes
* Test sorting by different fields
* Test filtering
2. Test the frontend implementation:
* Verify pagination controls work correctly
* Verify sorting works when clicking column headers
* Verify filters work as expected
* Check performance with larger datasets
4. Apply the Same Pattern to Other Entities
Once you've implemented pagination for clients, apply the same pattern to:
* Interfaces
* Mapping Rules
* Processed Files
* Any other list views in your application
This approach provides a complete server-side pagination implementation with sorting and filtering capabilities. It will significantly improve performance when dealing with large datasets and provide a better user experience

