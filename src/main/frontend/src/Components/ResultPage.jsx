import {Navigate} from 'react-router-dom';
import {useState, useEffect} from 'react';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import './Styles/ResultPage.css';
import SearchBar from './SearchBar';
import paragraphWithBoldWords from './paragraphWithBoldWords'
import results from '../db.json';

function ResultPage(props) {
    const [shouldNavigate, setShouldNavigate] = useState(false);
    const [shouldNavigateToAnotherSearch, setShouldNavigateToAnotherSearch] = useState(false);
    const [enableSuggestions, disableSuggestions] = useState(false);
    const [searchKey, setSearchKey] = useState(0);
    const [searchResults, setSearchResults] = useState([]);
    const [pagesNumber, setPagesNumber] = useState(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchTextCpy, setSearchTextCpy] = useState('');
    const returnToHome = () => {
        setShouldNavigate(true);
    }

    useEffect(() => {
        setSearchResults(results.results);
        setPagesNumber(Math.ceil(results.results.length / 10));
        setSearchTextCpy(props.searchText);
    }, []);

    useEffect(() => {
        disableSuggestions(false);
    }, [searchKey]);

    const handleSearch = (searchText) =>{
        setShouldNavigateToAnotherSearch(true);
        setSearchKey((prevKey) => prevKey + 1);
        props.handleSearch(searchText);
        setSearchTextCpy(searchText);
    }

    const handlePagination = (event, value) => {
        setCurrentPage(value);
    }

    return (
        <div className='Results-page'>
            <div className='Results-header'>
                <div>
                    <h2 onClick={returnToHome}>Search</h2>
                    <h2 onClick={returnToHome}> Engine</h2>
                </div>
                <SearchBar
                searchText={props.searchText}
                suggestionsFlag={enableSuggestions}
                setFlag={disableSuggestions}
                handleSearch={handleSearch}/>
                {shouldNavigate && <Navigate to='/'/>}
                {shouldNavigateToAnotherSearch && <Navigate to='/Results'/>}
            </div>
            <div className='Results-body'>
                <div>
                    <div className='aux-Info'>About {searchResults.length} results in 0.3 seconds</div>
                </div>
                {searchResults.length > 0 ? 
                searchResults.slice((currentPage - 1) *10, currentPage * 10).map((res, index)=>
                (<div className='Results' key={index}>
                    <div className='Result-block'>
                        <div className='Result-data'>
                            <h4 className='Result-heading'>{res.title}</h4>
                            <a className='Result-URL'>{res.url}</a>
                            <p className='Result-paragraph'>
                                {paragraphWithBoldWords(res.paragraph, searchTextCpy)}
                            </p>
                        </div>
                    </div>
                </div>))
                : (<div className='No-results Result-block'>
                    No results found
                </div>)}
            </div>
            <div className='Results-footer'>
                <Stack spacing={2}>
                    <Pagination
                    count={pagesNumber}
                    variant="outlined"
                    onChange={handlePagination}
                    size='medium'/>
                </Stack>
            </div>
        </div>
    );
}

export default ResultPage;