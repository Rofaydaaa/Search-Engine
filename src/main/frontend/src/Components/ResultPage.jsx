import { Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import './Styles/ResultPage.css';
import SearchBar from './SearchBar';
import paragraphWithBoldWords from './paragraphWithBoldWords'

function ResultPage(props) {
    const [shouldNavigate, setShouldNavigate] = useState(false);
    const [shouldNavigateToAnotherSearch, setShouldNavigateToAnotherSearch] = useState(false);
    const [enableSuggestions, disableSuggestions] = useState(false);
    const [searchKey, setSearchKey] = useState(0);
    const [searchResults, setSearchResults] = useState([]);
    const [pagesNumber, setPagesNumber] = useState(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchTextCpy, setSearchTextCpy] = useState('');
    const [time, setTime] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    const returnToHome = () => {
        setShouldNavigate(true);
    }

    useEffect(() => {
        setIsLoading(true);
        setSearchResults([]);
        setTime(0);
    },[]);

    useEffect(() => {
        setIsLoading(true);
        setSearchResults([]);
        setTime(0);
    }, [props.rerender]);

    useEffect(() => {
        if (isLoading && searchResults.length === 0 && props.searchResults.length > 0) {
            setSearchResults(props.searchResults);
            setPagesNumber(Math.ceil((props.searchResults.length - 1) / 10));
            setSearchTextCpy(props.searchText);

            // Check if the time property is defined before accessing it
            if (props.searchResults.length > 0 && props.searchResults[props.searchResults.length - 1].time !== undefined) {
                setTime(props.searchResults[props.searchResults.length - 1].time);
                setSearchResults(props.searchResults.slice(0, -1));
            } else {
                setSearchResults(props.searchResults);
            }

            setIsLoading(false);
        }
    }, [props.searchResults]);

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
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    }

    const getResults = (text) => {
        props.getResults(text);
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
                handleSearch={handleSearch}
                getResults={getResults}/>
                {shouldNavigate && <Navigate to='/'/>}
                {shouldNavigateToAnotherSearch && <Navigate to='/Results'/>}
            </div>
            <div className='Results-body'>
                {isLoading ? (
                    <div className='Loading-spinner'>
                        <div className='Loader'></div>
                    </div>
                ) : (
                    <>
                        <div>
                        {/* Check if the time property is defined before accessing it */}
                        {searchResults.length > 0 && time !== 0 && (
                            <div className='aux-Info'>About {searchResults.length} results in {time/1000} seconds</div>
                        )}
                        </div>
                        {searchResults.length > 0 ? 
                        searchResults.slice((currentPage - 1) *10, currentPage * 10).map((res, index)=>
                        (<div className='Results' key={index}>
                            <div className='Result-block'>
                                <div className='Result-data'>
                                    <h4 className='Result-heading'>{res.title}</h4>
                                    <a className='Result-URL'>{res.URL}</a>
                                    <p className='Result-paragraph'>
                                        {paragraphWithBoldWords(res.paragraph, searchTextCpy)}
                                    </p>
                                </div>
                            </div>
                        </div>))
                        : (<div className='No-results Result-block'>
                            No results found
                        </div>)}
                        {searchResults.length > 0 ? (
                            <div className='Results-footer'>
                                <Stack spacing={2}>
                                    <Pagination
                                    count={pagesNumber}
                                    variant="outlined"
                                    onChange={handlePagination}
                                    size='medium'/>
                                </Stack>
                            </div>
                        ) : null}
                    </>
                )}
            </div>
        </div>
    );
}

export default ResultPage;